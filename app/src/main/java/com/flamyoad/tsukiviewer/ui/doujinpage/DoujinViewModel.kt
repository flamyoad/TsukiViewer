package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.model.DoujinCollection
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DoujinViewModel(application: Application) : AndroidViewModel(application) {

    private val metadataRepo = MetadataRepository(application)

    private val collectionRepo = CollectionRepository(application)

    lateinit var detailWithTags: LiveData<DoujinDetailsWithTags>

    private val imageList = MutableLiveData<List<File>>()
    fun imageList(): LiveData<List<File>> = imageList

    private val coverImage = MutableLiveData<Uri>()
    fun coverImage(): LiveData<Uri> = coverImage

    private val collectionList = MutableLiveData<List<DoujinCollection>>()
    fun collectionList(): LiveData<List<DoujinCollection>> = collectionList

    val newCollectionName = MutableLiveData<String>()

    val collectionNameExists: LiveData<Boolean> = newCollectionName.switchMap { name ->
        return@switchMap collectionRepo.collectionNameExists(name)
    }

    val snackbarMsg = MutableLiveData<String>("")

    var currentPath: String = ""

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

    fun initCollectionList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (currentPath.isNotBlank()) {
                val dir = File(currentPath)
                val collections = collectionRepo.getAllCollectionsFrom(dir)

                withContext(Dispatchers.Main) {
                    collectionList.value = collections
                }
            }
        }
    }

    fun insertItemIntoTickedCollections(collectionWithTickStatus: HashMap<String, Boolean>) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = collectionRepo.wipeAndInsertNew(File(currentPath), collectionWithTickStatus)

            withContext(Dispatchers.Main) {
                snackbarMsg.value = status
            }
        }
    }

    fun createDefaultCollection() {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.createDefaultCollection()
        }
    }

    fun createCollection(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.insertCollection(DoujinCollection(name))
        }
    }
}