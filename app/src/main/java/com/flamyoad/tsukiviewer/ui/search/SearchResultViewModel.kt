package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.flamyoad.tsukiviewer.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.repository.DoujinRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.util.*

class SearchResultViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db: AppDatabase = AppDatabase.getInstance(app)
    private val context: Context = app.applicationContext

    private val bookmarkRepo = BookmarkRepository(app)
    private val doujinRepo = DoujinRepository(app)

    private val selectedDoujins = mutableListOf<Doujin>()

    private val pathDao: IncludedPathDao

    private val includedPathList: LiveData<List<IncludedPath>>

    private val doujinList = mutableListOf<Doujin>()

    private val searchResult = MutableLiveData<List<Doujin>>()
    fun searchedResult(): LiveData<List<Doujin>> = searchResult

    private val isLoading = MutableLiveData<Boolean>(false)
    fun isLoading(): LiveData<Boolean> = isLoading

    private val selectedDoujinsCount = MutableLiveData<Int>()
    fun selectionCountText(): LiveData<Int> = selectedDoujinsCount

    private var filterJob: Job? = null

    private var shouldResetSelections: Boolean = false

    private var shouldTickAllSelections: Boolean = false

    val snackbarText = MutableLiveData<String>("")

    val bookmarkGroupList = MutableLiveData<List<BookmarkGroup>>()

    val bookmarkGroupTickStatus = hashMapOf<String, Boolean>()

    init {
        pathDao = db.includedFolderDao()
        includedPathList = pathDao.getAll()

        viewModelScope.launch(Dispatchers.IO) {
            bookmarkGroupList.postValue(bookmarkRepo.getAllGroupsBlocking())
        }
    }

    @ExperimentalCoroutinesApi
    fun submitQuery(keyword: String, tags: String, shouldIncludeAllTags: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            doujinRepo.scanForDoujins(keyword, tags, shouldIncludeAllTags)
                .onStart { isLoading.postValue(true) }
                .onCompletion { isLoading.postValue(false) }
                .collect { postResult(it) }
        }
    }

    // Used in onNewIntent. Clears out previous list for the activity since it's singleTask
    @ExperimentalCoroutinesApi
    fun clearPrevAndSubmitQuery(keyword: String, tags: String, shouldIncludeAllTags: Boolean) {
        viewModelScope.launch {
            searchResult.value = emptyList()
            submitQuery(keyword, tags, shouldIncludeAllTags)
        }
    }

    /*  Filters the search result with keyword (again).
        User can only filter the list once the system has finished loading all items.
        (SearchView in the toolbar is hidden until all the items have finished loading)
    */
    fun filterList(keyword: String) {
        // Resets search result to full list if query is blank
        if (keyword.isBlank()) {
            searchResult.value = doujinList
        }

        filterJob?.cancel() // Cancels the job triggered by previous keyword
        filterJob = viewModelScope.launch {
            val filteredList = doujinList
                .filter { doujin -> doujin.title.toLowerCase(Locale.ROOT).contains(keyword) }
            searchResult.value = filteredList
        }
    }

    private suspend fun postResult(doujin: Doujin) {
        withContext(Dispatchers.Main) {
            if (!doujinList.contains(doujin)) {
                doujinList.add(doujin)
            }
            checkItemSelections()
            searchResult.value = doujinList
        }
    }

    private suspend fun checkItemSelections() {
        if (shouldResetSelections) {
            doujinList.forEach { item -> item.isSelected = false }

        } else if (shouldTickAllSelections) {
            doujinList.forEach { item -> item.isSelected = true }
            selectedDoujins.clear()
            selectedDoujins.addAll(doujinList)

            selectedDoujinsCount.value = selectedDoujins.size
            shouldTickAllSelections = false

        } else {
            // Had to move this to background thread.
            // It will hog UI if selectedDoujins has too many items
            withContext(Dispatchers.Default) {
                doujinList.forEach { item -> item.isSelected = (item in selectedDoujins) }
            }
        }

        searchResult.value = doujinList
        shouldResetSelections = false
    }
//
    fun tickSelectedDoujinsAll() {
        val hasFinishedLoading = isLoading.value == false

        if (hasFinishedLoading) {
            doujinList.forEach { item -> item.isSelected = true }
            searchResult.value = doujinList

            selectedDoujins.clear()
            selectedDoujins.addAll(doujinList)

            selectedDoujinsCount.value = selectedDoujins.size
        } else {
            shouldTickAllSelections = true
        }
    }

    fun tickSelectedDoujin(doujin: Doujin) {
        val hasBeenSelected = selectedDoujins.contains(doujin)
        when (hasBeenSelected) {
            true -> selectedDoujins.remove(doujin)
            false -> selectedDoujins.add(doujin)
        }
        selectedDoujinsCount.value = selectedDoujins.size

        val hasFinishedLoading = isLoading.value == false
        if (hasFinishedLoading) {
            val index = doujinList.indexOf(doujin)

            val doujin = doujinList[index]
            doujin.isSelected = !hasBeenSelected

            searchResult.value = doujinList // ??
        }
    }

    fun clearSelectedDoujins() {
        selectedDoujins.clear()

        val hasFinishedLoading = isLoading.value == false
        if (hasFinishedLoading) {
            for (doujin in doujinList) {
                doujin.isSelected = false
            }
            searchResult.value = doujinList

        } else {
            shouldResetSelections = true
        }
    }

    fun selectedCount(): Int {
        return selectedDoujins.size
    }

    fun getSelectedDoujins(): List<Doujin> {
        return selectedDoujins.toList()
    }

    fun fetchBookmarkGroup() {
        bookmarkGroupTickStatus.clear()

        if (selectedDoujins.size == 1) {
            val doujinPath = selectedDoujins.first().path

            viewModelScope.launch(Dispatchers.IO) {
                val tickedBookmarkGroups = bookmarkRepo.getAllGroupsFrom(doujinPath)
                withContext(Dispatchers.Main) {
                    for (group in tickedBookmarkGroups) {
                        if (group.isTicked) {
                            bookmarkGroupTickStatus.put(group.name, true)
                        }
                    }
                    bookmarkGroupList.value = tickedBookmarkGroups
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val allBookmarkGroups = bookmarkRepo.getAllGroupsBlocking()
                withContext(Dispatchers.Main) {
                    bookmarkGroupList.value = allBookmarkGroups
                }
            }
        }
    }

    fun insertItemIntoTickedCollections() {
        val tickedItems = bookmarkGroupTickStatus
            .filter { x -> x.value == true }
            .map { x -> x.key }

        val untickedItems = bookmarkGroupTickStatus
            .filter { x -> x.value == false }
            .map { x -> x.key }

        val bookmarkGroupsToBeAdded = tickedItems.minus(untickedItems)

        viewModelScope.launch(Dispatchers.IO) {
            val status: String
            if (selectedDoujins.size == 1) {
                val doujinPath = selectedDoujins.first().path
                status = bookmarkRepo.wipeAndInsertNew(doujinPath, bookmarkGroupTickStatus)
            } else {
                status = bookmarkRepo.insertAllItems(selectedDoujins.toList(), bookmarkGroupsToBeAdded)
            }

            withContext(Dispatchers.Main) {
                snackbarText.value = status
            }
        }
    }
}
