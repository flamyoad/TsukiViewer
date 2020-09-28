package com.flamyoad.tsukiviewer.ui.home.collection

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.DoujinCollection
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import com.flamyoad.tsukiviewer.utils.forceRefresh
import kotlinx.coroutines.*
import java.io.File

class CollectionDoujinViewModel(app: Application) : AndroidViewModel(app) {
    private val collectionRepo = CollectionRepository(app)

    private var hasBeenInitialized: Boolean = false

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    private val fullDoujinList: List<Doujin>

    private val collapsedItemsByHeader = hashMapOf<String, List<CollectionItem>>()

    private val itemsAndHeaders = MutableLiveData<List<CollectionItem>>()
    fun itemsWithHeaders(): LiveData<List<CollectionItem>> = itemsAndHeaders

    private val isLoading = MutableLiveData<Boolean>(false)
    fun isLoading(): LiveData<Boolean> = isLoading

    val newCollectionName = MutableLiveData<String>()

    val collectionNameIsUsed: LiveData<Boolean> = newCollectionName.switchMap { name ->
        return@switchMap collectionRepo.collectionNameExists(name)
    }

    val allItems: LiveData<List<CollectionItem>>

    val selectedItemsList = mutableListOf<CollectionItem>()

    init {
        allItems = collectionRepo.getAllItems().distinctUntilChanged()
        fullDoujinList = (app as MyApplication).fullDoujinList ?: emptyList()
    }

    fun initList() {
        isLoading.value = true

        if (hasBeenInitialized) {
            reloadList()

        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val collections = collectionRepo.getAllCollectionsBlocking()

                val list = mutableListOf<CollectionItem>()

                for (collection in collections) {
                    val itemsInCollection = collectionRepo.getAllItemsFrom(collection)

                    list.add(getHeaderItem(collection.name))

                    withContext(Dispatchers.Main) {
                        itemsAndHeaders.value = list
                    }

                    for (item in itemsInCollection) {
                        val actualItem = CollectionItem(
                            id = item.id,
                            collectionName = item.collectionName,
                            isHeader = false,
                            absolutePath = item.absolutePath,
                            doujin = getDoujin(item.absolutePath)
                        )

                        list.add(actualItem)

                        withContext(Dispatchers.Main) {
                            itemsAndHeaders.value = list
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    hasBeenInitialized = true
                    isLoading.value = false
                }
            }
        }
    }

    private fun reloadList() {
        val oldList = getExpandedItems() // From expanded items
        oldList.addAll(collapsedItemsByHeader.values.flatten()) // From collapsed items

        viewModelScope.launch(Dispatchers.IO) {
            val newList = mutableListOf<CollectionItem>()

            val collections = collectionRepo.getAllCollectionsBlocking()

            for (collection in collections) {
                val existingHeader = oldList.find { x ->
                    x.collectionName == collection.name && x.isHeader
                }

                if (existingHeader != null) {
                    newList.add(existingHeader)
                } else {
                    newList.add(getHeaderItem(collection.name))
                }

                val itemsInCollection = collectionRepo.getAllItemsFrom(collection)

                for (item in itemsInCollection) {
                    val existingItem = oldList.find { x -> x.id == item.id }

                    if (existingItem != null) {
                        newList.add(existingItem)
                    } else {
                        val newItem = CollectionItem(
                            id = item.id,
                            collectionName = item.collectionName,
                            isHeader = false,
                            absolutePath = item.absolutePath,
                            doujin = getDoujin(item.absolutePath)
                        )

                        newList.add(newItem)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                itemsAndHeaders.value = newList
                isLoading.value = false
            }
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

        // fileList might be null here, try catch
        val imageList = fileList.filter { f -> f.extension in imageExtensions }

        val coverImage = imageList.first().toUri()
        val title = dir.name
        val numberOfImages = imageList.size
        val lastModified = dir.lastModified()

        val doujin = Doujin(coverImage, title, numberOfImages, lastModified, dir)

        return doujin
    }

    fun toggleHeader(header: CollectionItem, headerPosition: Int) {
        when (header.isCollapsed) {
            true -> expandItems(header, headerPosition)
            false -> collapseItems(header, headerPosition)
        }
    }

    private fun collapseItems(header: CollectionItem, headerPosition: Int) {
        val collectionName = header.collectionName

        val itemList = getExpandedItems()

        val lastItem = itemList.findLast { item -> item.collectionName == collectionName }
        val lastItemIndex = itemList.indexOf(lastItem)

        // fromIndex (inclusive) and toIndex (exclusive)
        val collapsedItems = itemList.subList(headerPosition + 1, lastItemIndex + 1).toList()
        collapsedItemsByHeader.put(collectionName, collapsedItems)

        itemList.removeAll { item -> !item.isHeader && item.collectionName == collectionName }

        header.isCollapsed = !header.isCollapsed

        itemsAndHeaders.value = itemList
    }

    private fun expandItems(header: CollectionItem, headerPosition: Int) {
        val collectionName = header.collectionName

        header.isCollapsed = !header.isCollapsed

        val collapsedItems = collapsedItemsByHeader[collectionName]

        if (collapsedItems != null) {
            val itemList = getExpandedItems()

            itemList.addAll(headerPosition + 1, collapsedItems)
            itemsAndHeaders.value = itemList
        }
    }

    // Includes headers
    private fun getExpandedItems(): MutableList<CollectionItem> {
        return itemsAndHeaders.value?.toMutableList() ?: mutableListOf()
    }

    fun onItemSelected(selectedItem: CollectionItem) {
        val originalStatus = selectedItem.isSelected

        val itemList = getExpandedItems().toMutableList()

        if (selectedItemsList.contains(selectedItem)) {
            selectedItemsList.remove(selectedItem)
        } else {
            selectedItemsList.add(selectedItem)
        }

        // This long ass code has Something something to do with references.
        // Otherwise both ::isSelected has same value when evaluated in DiffItemCallback
        val oldItemIndex = itemList.indexOf(selectedItem)
        itemList.remove(selectedItem)

        val newItem = selectedItem.copy().apply {
            isSelected = !originalStatus
        }

        itemList.add(oldItemIndex, newItem)

        itemsAndHeaders.value = itemList
        itemsAndHeaders.forceRefresh()
    }

    fun clearActionModeData() {
        Log.d("debugz", "clearActionModeData() is called")
        selectedItemsList.clear()

        collapsedItemsByHeader.forEach { (header, items) ->
            for (item in items) {
                item.isSelected = false
            }
        }

        val newList = getExpandedItems().map { x ->
            x.copy().apply { isSelected = false }
        }

        itemsAndHeaders.value = newList
        itemsAndHeaders.forceRefresh()
    }

    fun getSelectedItemCount(): Int {
        return selectedItemsList.size
    }

    fun getSelectedItemNames(): Array<CharSequence> {
        return selectedItemsList
            .map { item -> item.doujin?.title ?: "" }
            .toTypedArray()
    }

    /*
     ******************************************************************
     * OK
     ******************************************************************
     */
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

    fun deleteItems() {
        Log.d("debugz", "deleteItems() is called")
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.itemDao.delete(selectedItemsList.toList())
        }
    }

    /*
     ******************************************************************
     * OK
     ******************************************************************
     */
}