package com.flamyoad.tsukiviewer.ui.home.bookmarks

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.BookmarkItem
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class BookmarkViewModel(app: Application) : AndroidViewModel(app) {
    private val bookmarkRepo = BookmarkRepository(app)

    private val fullDoujinList: List<Doujin>

    private val selectedBookmarks: MutableList<BookmarkItem> = mutableListOf()

    private var fetchJob: Job? = null
    private var filterJob: Job? = null

    private var filterWord: String = ""

    private var bookmarkItemsBeforeFilter = mutableListOf<BookmarkItem>()

    private val bookmarkGroups = MutableLiveData<List<BookmarkGroup>>()
    fun bookmarkGroups(): LiveData<List<BookmarkGroup>> = bookmarkGroups

    val bookmarkItems: LiveData<List<BookmarkItem>>

    private val processedBookmarks = MutableLiveData<MutableList<BookmarkItem>>()
    fun processedBookmarks(): LiveData<MutableList<BookmarkItem>> = processedBookmarks

    private val isLoading = MutableLiveData<Boolean>(false)
    fun isLoading(): LiveData<Boolean> = isLoading

    private val snackBarText = MutableLiveData<String>()
    fun snackBarText(): LiveData<String> = snackBarText.distinctUntilChanged()

    val newGroupName = MutableLiveData<String>()

    val groupNameIsUsed: LiveData<Boolean> = newGroupName.switchMap { name ->
        return@switchMap bookmarkRepo.groupNameExists(name)
    }

    private val selectedBookmarkGroup = MutableLiveData<BookmarkGroup>()
    fun selectedBookmarkGroup(): LiveData<BookmarkGroup> = selectedBookmarkGroup.distinctUntilChanged()

    var selectedGroup: BookmarkGroup? = null

    var selectedGroupName: String = ""

    val groupList: LiveData<List<BookmarkGroup>>

    init {
        groupList = bookmarkRepo.getAllGroups()
        fullDoujinList = (app as MyApplication).fullDoujinList ?: emptyList()
        bookmarkItems = bookmarkRepo.getAllItems()
    }

    fun initializeGroups(groups: List<BookmarkGroup>) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                selectedBookmarkGroup.value = groups.firstOrNull() ?: return@withContext
                selectedGroup = groups.firstOrNull()
            }

            val newList = mutableListOf<BookmarkGroup>()

            for (group in groups) {
                val bookmarkedItems = bookmarkRepo.getAllItemsFrom(group)

                val pic = getThumbnail(bookmarkedItems.firstOrNull())

                val bookmarkGroup = BookmarkGroup(
                    name = group.name,
                    pic = pic,
                    totalItems = bookmarkedItems.size,
                    lastDate = group.lastDate,
                    isTicked = false
                )
                newList.add(bookmarkGroup)
            }
            withContext(Dispatchers.Main) {
                bookmarkGroups.value = newList
            }
        }
    }

    fun refreshGroupInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val groups = bookmarkRepo.getAllGroupsBlocking()

            val newList = mutableListOf<BookmarkGroup>()

            for (group in groups) {
                val bookmarkedItems = bookmarkRepo.getAllItemsFrom(group)

                val pic = getThumbnail(bookmarkedItems.firstOrNull())

                val bookmarkGroup = BookmarkGroup(
                    name = group.name,
                    pic = pic,
                    totalItems = bookmarkedItems.size,
                    lastDate = group.lastDate,
                    isTicked = false
                )
                newList.add(bookmarkGroup)
            }
            withContext(Dispatchers.Main) {
                bookmarkGroups.value = newList
            }
        }
    }

    fun fetchBookmarkItems(firstGroup: BookmarkGroup) {
        fetchJob?.cancel()
        isLoading.value = true

        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            val bookmarkList = when (selectedGroupName.isBlank()) {
                true -> bookmarkRepo.getAllItemsFrom(firstGroup)
                false -> bookmarkRepo.getAllItemsFrom(selectedGroupName)
            }

            val itemList = mutableListOf<BookmarkItem>()
            for (bookmark in bookmarkList) {
                val item = bookmark.copy(doujin = getDoujin(bookmark.absolutePath))

                if (item in selectedBookmarks) {
                    item.isSelected = true
                }
                itemList.add(item)
            }

            withContext(Dispatchers.Main) {
                processedBookmarks.value = itemList
                bookmarkItemsBeforeFilter = itemList

                isLoading.value = false

                filterList(filterWord)
            }
        }
    }

    private fun getThumbnail(item: BookmarkItem?): Uri {
        val dir = item?.absolutePath

        if (dir == null) {
            return Uri.EMPTY
        }

        if (fullDoujinList.isNotEmpty()) {
            val doujin = fullDoujinList.find { x -> x.path == dir }
            return doujin?.pic ?: Uri.EMPTY

        } else {
            val images = dir.listFiles(ImageFileFilter())
            return images?.firstOrNull()?.toUri() ?: Uri.EMPTY
        }
    }

    fun filterList(query: String) {
        filterJob?.cancel()
        filterWord = query

        if (query.isBlank()) {
            processedBookmarks.value = bookmarkItemsBeforeFilter
        }

        filterJob = viewModelScope.launch(Dispatchers.Default) {
            val filteredList = bookmarkItemsBeforeFilter.filter { x ->
                x.doujin?.title?.toLowerCase(Locale.ROOT)?.contains(query) ?: false
            }

            withContext(Dispatchers.Main) {
                processedBookmarks.value = filteredList.toMutableList()
            }
        }
    }

    fun tickSelectedBookmark(item: BookmarkItem) {
        val hasBeenSelected = selectedBookmarks.contains(item)
        when (hasBeenSelected) {
            true -> selectedBookmarks.remove(item)
            false -> selectedBookmarks.add(item)
        }

        val currentList = processedBookmarks.value ?: return

        // The selected object needs to be recreated
        // Otherwise both ::isSelected has same value when evaluated in DiffItemCallback
        val oldItemIndex = currentList.indexOf(item)
        currentList.remove(item)

        val newItem = item.copy().apply {
            isSelected = !hasBeenSelected
        }

        currentList.add(oldItemIndex, newItem)

        processedBookmarks.value = currentList
    }

    fun clearSelectedBookmarks() {
        selectedBookmarks.clear()

        val currentList = processedBookmarks.value ?: return
        val refreshedList = currentList.map { x ->
            x.copy().apply { isSelected = false }
        }

        processedBookmarks.value = refreshedList.toMutableList()
    }

    fun selectedBookmarkCount(): Int {
        return selectedBookmarks.size
    }

    fun selectedBookmarkNames(): Array<CharSequence> {
        return selectedBookmarks
            .map { x -> x.doujin?.title ?: "" }
            .toTypedArray()
    }

    fun deleteItems() {
        val list = selectedBookmarks.toList()
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepo.itemDao.delete(list)
        }
    }

    fun switchBookmarkGroup(group: BookmarkGroup) {
        selectedGroup = group
        selectedGroupName = group.name

        selectedBookmarkGroup.value = group // replace
    }

    fun deleteGroup(group: BookmarkGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepo.removeGroup(group)
        }
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
        val imageList = dir.listFiles(ImageFileFilter())

        val coverImage = imageList?.firstOrNull()?.toUri() ?: Uri.EMPTY
        val title = dir.name
        val numberOfImages = imageList?.size ?: 0
        val lastModified = dir.lastModified()

        val doujin = Doujin(coverImage, title, numberOfImages, lastModified, dir)

        return doujin
    }

    fun createCollection(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepo.insertGroup(BookmarkGroup(name))
        }
    }

    fun changeGroupName(oldName: String, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepo.changeGroupName(oldName, newName)
        }
        selectedGroupName = newName
    }
}