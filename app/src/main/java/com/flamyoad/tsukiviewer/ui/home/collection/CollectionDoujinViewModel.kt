package com.flamyoad.tsukiviewer.ui.home.collection

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.DoujinCollection
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CollectionDoujinViewModel(app: Application) : AndroidViewModel(app) {

    private val collectionRepo = CollectionRepository(app)

    private val tempItems: MutableList<CollectionItem> = mutableListOf()

    private val fullDoujinList: List<Doujin>

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    private val itemsWithHeaders = MutableLiveData<List<CollectionItem>>()
    fun itemsWithHeaders(): LiveData<List<CollectionItem>> = itemsWithHeaders

    val newCollectionName = MutableLiveData<String>()

    val collectionNameExists: LiveData<Boolean> = newCollectionName.switchMap { name ->
        return@switchMap collectionRepo.collectionNameExists(name)
    }

    val itemsNoHeaders: LiveData<List<CollectionItem>>

    init {
        itemsNoHeaders = collectionRepo.getAllItems()
        fullDoujinList = (app as MyApplication).fullDoujinList ?: emptyList()
    }

    fun refreshList() {
        viewModelScope.launch(Dispatchers.IO) {
            val collections = collectionRepo.getAllCollectionsBlocking()

            val list = mutableListOf<CollectionItem>()

            for (collection in collections) {
                list.add(getHeaderItem(collection.name))

                val itemsInCollection = collectionRepo.getAllItemsFrom(collection)

                for (item in itemsInCollection) {
                    val actualItem = CollectionItem(
                        id = item.id,
                        collectionName = item.collectionName,
                        isHeader = false,
                        absolutePath = item.absolutePath,
                        doujin = getDoujin(item.absolutePath)
                    )
                    list.add(actualItem)
                }
            }
            itemsWithHeaders.postValue(list)
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

    private fun getDoujin(dir: File): Doujin {
        //  Find from existing list first before scanning from directory
        if (fullDoujinList.isNotEmpty()) {
            val doujin = fullDoujinList.find { doujin -> doujin.path == dir }
            if (doujin != null) {
                return doujin
            }
        }

        //  If not found, then we have to use Java File api
        val fileList = dir.listFiles()

        val imageList = fileList.filter { f -> f.extension in imageExtensions }

        val coverImage = imageList.first().toUri()
        val title = dir.name
        val numberOfImages = imageList.size
        val lastModified = dir.lastModified()

        val doujin = Doujin(coverImage, title, numberOfImages, lastModified, dir)

        return doujin
    }

    fun createCollection(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.insertCollection(DoujinCollection(name))
        }
    }

    fun changeCollectionName(oldName: String, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.changeCollectionName(oldName, newName)
        }
    }

    fun deleteCollection(name: String) {
        if (name.isBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.deleteCollection(name)
        }
    }

    fun deleteItems(items: List<CollectionItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.itemDao.delete(items)
        }
    }

}