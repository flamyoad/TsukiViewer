package com.flamyoad.tsukiviewer.ui.home.collection

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CollectionDoujinViewModel(application: Application) : AndroidViewModel(application) {

    private val collectionRepo = CollectionRepository(application)

    private val tempItems: MutableList<CollectionItem> = mutableListOf()

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    private val itemsWithHeaders = MutableLiveData<List<CollectionItem>>()

    fun itemsWithHeaders(): LiveData<List<CollectionItem>> = itemsWithHeaders

    val itemsNoHeaders: LiveData<List<CollectionItem>>

    init {
        itemsNoHeaders = collectionRepo.getAllItems()
    }

    fun refreshList() {
        viewModelScope.launch(Dispatchers.IO) {
            val collections = collectionRepo.getAllCollectionBlocking()
            for (collection in collections) {
                postResult(getHeaderItem(collection.name))

                val itemsInCollection = collectionRepo.getAllItemsFrom(collection)

                for (item in itemsInCollection) {
                    val actualItem = CollectionItem(
                        id = item.id,
                        collectionName = item.collectionName,
                        isHeader = false,
                        absolutePath = item.absolutePath,
                        doujin = getDoujin(item.absolutePath)
                    )
                    postResult(actualItem)
                }
            }
        }
    }

    private fun postResult(item: CollectionItem) {
        if (!tempItems.contains(item)) {
            tempItems.add(item)
            itemsWithHeaders.postValue(tempItems.toList())
        }
    }

    private fun getHeaderItem(name: String): CollectionItem {
        return CollectionItem(
            isHeader = true,
            collectionName = name,
            absolutePath = File("")
        )
    }

    private fun getDoujin(currentDir: File): Doujin {
        val fileList = currentDir.listFiles()

        val imageList = fileList.filter { f -> f.extension in imageExtensions }

        val coverImage = imageList.first().toUri()
        val title = currentDir.name
        val numberOfImages = imageList.size
        val lastModified = currentDir.lastModified()

        val doujin = Doujin(coverImage, title, numberOfImages, lastModified, currentDir)

        return doujin
    }

}