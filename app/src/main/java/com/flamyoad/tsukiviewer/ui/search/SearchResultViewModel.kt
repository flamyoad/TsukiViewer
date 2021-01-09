package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContentResolverCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.DoujinTagsDao
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.flamyoad.tsukiviewer.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import com.flamyoad.tsukiviewer.utils.toDoujin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class SearchResultViewModel(private val app: Application) : AndroidViewModel(app) {
    private val context: Context = app.applicationContext
    private val contentResolver: ContentResolver = app.contentResolver
    private val bookmarkRepo = BookmarkRepository(app)
    private val db: AppDatabase = AppDatabase.getInstance(app)

    private val selectedDoujins = mutableListOf<Doujin>()

    private val pathDao: IncludedPathDao
    private val doujinDetailsDao: DoujinDetailsDao
    private val tagDao: TagDao
    private val doujinTagDao: DoujinTagsDao

    private val includedPathList: LiveData<List<IncludedPath>>

    // DO not modify this list in any other places than the coroutine started in submitQuery() function
    private val doujinList = mutableListOf<Doujin>()

    private val searchResult = MutableLiveData<List<Doujin>>()

    private val isLoading = MutableLiveData<Boolean>(false)

    private var loadingJob: Job? = null

    private var filterJob: Job? = null

    private var shouldResetSelections: Boolean = false

    fun searchedResult(): LiveData<List<Doujin>> = searchResult

    fun isLoading(): LiveData<Boolean> = isLoading

    val snackbarText = MutableLiveData<String>("")

    val bookmarkGroupList: LiveData<List<BookmarkGroup>>

    init {
        pathDao = db.includedFolderDao()
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()
        doujinTagDao = db.doujinTagDao()

        includedPathList = pathDao.getAll()
        bookmarkGroupList = bookmarkRepo.getAllGroups()
    }

    fun submitQuery(keyword: String, tags: String, shouldIncludeAllTags: Boolean) {
        if (loadingJob != null) return

        isLoading.value = true

        loadingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val keywordLowerCase = keyword.toLowerCase(Locale.ROOT)

                searchFromDatabase(keywordLowerCase, tags, shouldIncludeAllTags)

                // If tags are not specified in the query, then we have to search from file explorer too
                if (tags.isBlank()) {
                    val existingList = (app as MyApplication).fullDoujinList
                    if (existingList != null) {
                        searchFromExistingList(existingList.toList(), keywordLowerCase)
                    } else {
                        searchFromFileExplorer(keywordLowerCase)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                isLoading.value = false
            }
        }
    }

    private suspend fun searchFromDatabase(keyword: String, tags: String, shouldIncludeAllTags: Boolean) {
        if (keyword.isNotBlank() && tags.isNotBlank()) {
            // Search using both title and tags
            val tagList = tags.split(",")
                .map { tagName -> tagName }

            val doujinDetailItems = when (shouldIncludeAllTags) {
                true -> doujinDetailsDao.findByTags(tagList, tagList.size) // Searched items must include all tags
                false -> doujinDetailsDao.findByTags(tagList) // Searched items must include at least 1 tag
            }

            for (item in doujinDetailItems) {
                val containsKeywordEnglish =
                    item.fullTitleEnglish.toLowerCase(Locale.ROOT).contains(keyword)
                val containsKeywordJap = item.fullTitleJapanese.contains(keyword)

                if (containsKeywordEnglish || containsKeywordJap) {
                    postResult(item.absolutePath)
                }
            }

        } else if (keyword.isNotBlank() && tags.isBlank()) {
            // Search using title only
            val doujinDetailItems = doujinDetailsDao.findByTitle(keyword)

            for (item in doujinDetailItems) {
                postResult(item.absolutePath)
            }

        } else if (tags.isNotBlank() && keyword.isBlank()) {
            // Search using tags only
            val tagList = tags.split(",")
                .map { tagName -> tagName }

            val doujinDetailItems = when (shouldIncludeAllTags) {
                true -> doujinDetailsDao.findByTags(tagList, tagList.size)
                false -> doujinDetailsDao.findByTags(tagList)
            }

            for (item in doujinDetailItems) {
                postResult(item.absolutePath)
            }
        }
    }

    private suspend fun searchFromFileExplorer(keyword: String) {
        val includedDirs = pathDao.getAllBlocking()
        for (dir in includedDirs) {
            val pathName = dir.toString()

            val uri = MediaStore.Files.getContentUri("external")

            val projection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.PARENT
            )

            val selection =
                "${MediaStore.Files.FileColumns.DATA} LIKE ?" +
                        " AND " +
                        "${MediaStore.Files.FileColumns.TITLE} LIKE ?"

            val params = arrayOf(
                "%" + pathName + "%",
                "%" + keyword + "%"
            )

            val cursor = ContentResolverCompat.query(
                contentResolver,
                uri,
                projection,
                selection,
                params,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val idSet = mutableSetOf<String>()

                val fullPath =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val parentId =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT))

                if (idSet.add(parentId)) {
                    val doujinDir = File(fullPath)

                    val imageList = doujinDir.listFiles(ImageFileFilter())

                    if (!imageList.isNullOrEmpty()) {
                        val doujin = Doujin(
                            pic = imageList.first().toUri(),
                            title = doujinDir.name,
                            path = doujinDir,
                            lastModified = doujinDir.lastModified(),
                            numberOfItems = imageList.size
                        )
                        postResult(doujin)
                    }
                }
                Log.d("cursor", "Full Path: ${fullPath}, Parent index: ${parentId}")
            }
        }
    }

    private suspend fun searchFromExistingList(doujinList: List<Doujin>, keyword: String) {
        for (doujin in doujinList) {
            if (doujin.title.toLowerCase(Locale.ROOT).contains(keyword)) {
                postResult(doujin)
            }
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
        if (!doujinList.contains(doujin)) {
            doujinList.add(doujin)
        }

        withContext(Dispatchers.Main) {
            checkItemSelections()
            searchResult.value = doujinList
        }
    }

    private suspend fun postResult(dir: File) {
        val isDuplicate = doujinList.any { doujin -> doujin.title == dir.name }
        if (!isDuplicate) {
            val doujin = dir.toDoujin()
            if (doujin != null) {
                doujinList.add(doujin)
            }

            withContext(Dispatchers.Main) {
                checkItemSelections()
                searchResult.value = doujinList
            }
        }
    }

    private fun checkItemSelections() {
        if (shouldResetSelections) {
            for (doujin in doujinList) {
                doujin.isSelected = false
            }
        } else {
            for (doujin in doujinList) {
                doujin.isSelected = doujin in selectedDoujins
            }
        }
        searchResult.value = doujinList
        shouldResetSelections = false
    }

    fun tickSelectedDoujin(doujin: Doujin) {
        val hasBeenSelected = selectedDoujins.contains(doujin)
        when (hasBeenSelected) {
            true -> selectedDoujins.remove(doujin)
            false -> selectedDoujins.add(doujin)
        }

        if (loadingJob?.isCompleted == true) {

            val index = doujinList.indexOf(doujin)

            val doujin = doujinList[index]
            doujin.isSelected = !hasBeenSelected

            searchResult.value = doujinList // ??
        }
    }

    fun clearSelectedDoujins() {
        selectedDoujins.clear()

        if (loadingJob?.isCompleted == true) {
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

    fun insertItemIntoTickedCollections(bookmarkGroups: List<BookmarkGroup>) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = bookmarkRepo.insertAllItems(selectedDoujins.toList(), bookmarkGroups)

            withContext(Dispatchers.Main) {
                snackbarText.value = status
            }
        }
    }
}
