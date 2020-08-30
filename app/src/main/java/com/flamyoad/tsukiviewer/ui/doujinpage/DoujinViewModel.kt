package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.model.DoujinCollection
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class DoujinViewModel(application: Application) : AndroidViewModel(application) {

    private val metadataRepo = MetadataRepository(application)

    private val collectionRepo = CollectionRepository(application)

    lateinit var detailWithTags: LiveData<DoujinDetailsWithTags>

    private val imageList = MutableLiveData<List<File>>()

    private val coverImage = MutableLiveData<Uri>()

    var currentPath: String = ""

    val tickedCollections = mutableListOf<DoujinCollection>()

    fun imageList(): LiveData<List<File>> = imageList

    fun coverImage(): LiveData<Uri> = coverImage

    fun scanForImages(dirPath: String) {
        if (dirPath == currentPath) {
            return
        } else {
            currentPath = dirPath
        }

        val dir = File(dirPath)

        detailWithTags = metadataRepo
            .doujinDetailsDao
            .getLongDetailsByPath(dir.toString())

        val fetchedImages = dir.listFiles(ImageFileFilter())

        /*  sortedBy { x -> x.name } will return the following wrong result:
            ['0.jpg', '1.jpg', '10.jpg', '11.jpg', '12.jpg' . . .]

            This is because sortedBy() is using ASCII order to sort.
            Workaround is to use Natural Sort

            Article describing the differences about programming language's in-built sort and natural sort
            https://blog.codinghorror.com/sorting-for-humans-natural-sort-order/

            Sort filenames in directory in ascending order [duplicate]
            https://stackoverflow.com/questions/33159106/sort-filenames-in-directory-in-ascending-order
         */
        val naturalSort =
            compareBy<File> { it.name.length } // If you don't first compare by length, it won't work
                .then(naturalOrder())

        imageList.value = fetchedImages.sortedWith(naturalSort)

        val firstImage = fetchedImages.first().toUri()
        coverImage.value = firstImage
    }

    fun detailsNotExists(): Boolean {
        return detailWithTags.value == null
    }

    fun resetTags() {
        val dir = File(currentPath)
        viewModelScope.launch {
            metadataRepo.resetTags(dir)
        }
    }

    fun getAllCollections(): LiveData<List<DoujinCollection>> {
        return collectionRepo.getAllCollection()
    }

    fun insertNewCollection(collection: DoujinCollection) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.insertCollection(collection)
        }
    }

    fun insertItemIntoTickedCollections() {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.wipeAndInsertNew(File(currentPath), tickedCollections)
        }
    }

    fun initializeDefaultCollection() {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.initializeCollection()
        }
    }
}