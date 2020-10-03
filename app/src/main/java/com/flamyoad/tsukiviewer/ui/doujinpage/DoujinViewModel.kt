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

    val snackbarText = MutableLiveData<String>("")

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

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val fetchedImages = dir.listFiles(ImageFileFilter())

                val naturalSort =
                    compareBy<File> { it.name.length } // If you don't first compare by length, it won't work
                        .then(naturalOrder())
                /*
                    sortedBy { x -> x.name } will return the following wrong result:
                    ['0.jpg', '1.jpg', '10.jpg', '11.jpg', '12.jpg' . . .]

                    This is because sortedBy() is using ASCII order to sort.
                    Workaround is to use Natural Sort

                    Article describing the differences about programming language's in-built sort and natural sort
                    https://blog.codinghorror.com/sorting-for-humans-natural-sort-order/

                    Sort filenames in directory in ascending order [duplicate]
                    https://stackoverflow.com/questions/33159106/sort-filenames-in-directory-in-ascending-order
                */

                val sortedImages = fetchedImages.sortedWith(naturalSort)

                withContext(Dispatchers.Main) {
                    imageList.value = sortedImages

                    val firstImage = fetchedImages.first().toUri()
                    coverImage.value = firstImage
                }
            }
        }
    }

    fun detailsNotExists(): Boolean {
        return this::detailWithTags.isInitialized
    }

    fun resetTags() {
        val dir = File(currentPath)
        viewModelScope.launch {
            metadataRepo.resetTags(dir)
        }
    }

    fun removeMetadata() {
        val data = detailWithTags.value?.doujinDetails

        if (data != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val doujinDetails =
                        metadataRepo.removeMetadata(data)
                }
            }
        } else {
            snackbarText.value = "Failed to remove existing title & tags"
            snackbarText.value = "" // Clears the value
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
            val status =
                collectionRepo.wipeAndInsertNew(File(currentPath), collectionWithTickStatus)

            withContext(Dispatchers.Main) {
                snackbarText.value = status
            }
        }
    }

    fun createCollection(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.insertCollection(DoujinCollection(name))
        }
    }

    fun getNukeCode(): String? {
        val nukeCode = detailWithTags.value?.doujinDetails?.nukeCode.toString()
        return if (nukeCode == "null") // Room actually returns a null string lul
            null
        else
            nukeCode
    }
}