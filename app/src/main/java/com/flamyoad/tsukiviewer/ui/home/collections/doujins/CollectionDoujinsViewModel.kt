package com.flamyoad.tsukiviewer.ui.home.collections.doujins

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
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import com.flamyoad.tsukiviewer.utils.toDoujin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class CollectionDoujinsViewModel(private val app: Application) : AndroidViewModel(app) {
    private val context: Context = app.applicationContext
    private val contentResolver: ContentResolver = app.contentResolver

    private val db: AppDatabase = AppDatabase.getInstance(app)
    private val doujinDetailsDao: DoujinDetailsDao
    private val pathDao: IncludedPathDao

    private val bookmarkRepo = BookmarkRepository(app)
    private val collectionRepo = CollectionRepository(app)

    val bookmarkGroupList: LiveData<List<BookmarkGroup>>

    private val doujinList = mutableListOf<Doujin>()

    private val selectedDoujins = mutableListOf<Doujin>()

    private val searchResult = MutableLiveData<List<Doujin>>()
    fun searchedResult(): LiveData<List<Doujin>> = searchResult

    private val isLoading = MutableLiveData<Boolean>(false)
    fun isLoading(): LiveData<Boolean> = isLoading

    val snackbarText = MutableLiveData<String>("")

    private var loadingJob: Job? = null

    private var filterJob: Job? = null

    private var shouldResetSelections: Boolean = false

    private var titleKeywords: List<String> = emptyList()
    private var mustHaveDirs: List<File> = emptyList()
    private var minNumberPages: Int = Int.MIN_VALUE
    private var maxNumberPages: Int = Int.MAX_VALUE

    init {
        bookmarkGroupList = bookmarkRepo.getAllGroups()
        pathDao = db.includedFolderDao()
        doujinDetailsDao = db.doujinDetailsDao()
    }

    fun submitQuery(collectionId: Long) {
        if (loadingJob != null) return

        isLoading.value = true

        loadingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val collection = collectionRepo.get(collectionId)

                minNumberPages = collection.minNumPages
                maxNumberPages = collection.maxNumPages

                val includedTags = collectionRepo.getIncludedTags(collectionId)
                val excludedTags = collectionRepo.getExcludedTags(collectionId)

                titleKeywords = collectionRepo.getTitles(collectionId)
                    .map { title -> title.toLowerCase(Locale.ROOT) }
                mustHaveDirs = collectionRepo.getDirectories(collectionId)

                searchFromDatabase(
                    includedTags, collection.mustHaveAllIncludedTags,
                    excludedTags, collection.mustHaveAllExcludedTags
                )

                // If tags are not specified in the query, then we have to search from file explorer too
                if (includedTags.isEmpty() && excludedTags.isEmpty()) {
                    val existingList = (app as MyApplication).fullDoujinList
                    if (existingList != null) {
                        searchFromExistingList(existingList.toList(), titleKeywords)
                    } else {
                        searchFromFileExplorer(titleKeywords)
                    }
                }

                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    private suspend fun searchFromFileExplorer(titleFilters: List<String>) {
        val includedDirs = pathDao.getAllBlocking()
        for (dir in includedDirs) {
            val pathName = dir.toString()

            val uri = MediaStore.Files.getContentUri("external")

            val projection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.PARENT
            )

            var selection = "${MediaStore.Files.FileColumns.DATA} LIKE ?"

            for (title in titleFilters) {
                selection += " AND " + "${MediaStore.Files.FileColumns.TITLE} LIKE ?"
            }

            val keywordArray = titleFilters.map { title -> "%" + title + "%" }
                .toTypedArray()

            val params = arrayOf("%" + pathName + "%", *keywordArray)

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

    private suspend fun searchFromDatabase(
        includedTags: List<Tag>, mustHaveAllIncludedTags: Boolean,
        excludedTags: List<Tag>, mustHaveAllExcludedTags: Boolean
    ) {
        val doujinList = when {
            mustHaveAllIncludedTags && mustHaveAllExcludedTags -> collectionRepo.searchIncludedAndExcludedAnd(
                includedTags,
                excludedTags
            )

            !mustHaveAllIncludedTags && mustHaveAllExcludedTags -> collectionRepo.searchIncludedOrExcludedAnd(
                includedTags,
                excludedTags
            )

            mustHaveAllIncludedTags && !mustHaveAllExcludedTags -> collectionRepo.searchIncludedAndExcludedOr(
                includedTags,
                excludedTags
            )

            else -> collectionRepo.searchIncludedOrExcludedOr(includedTags, excludedTags)
        }

        for (doujin in doujinList) {
            postResult(doujin.absolutePath)
        }
    }

    private suspend fun searchFromExistingList(doujinList: List<Doujin>, keywordList: List<String>) {
        for (doujin in doujinList) {
            if (doujin.hasFulfilledCriteria()) {
                postResult(doujin)
            }
        }
    }

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

    private suspend fun postResult(doujin: Doujin) {
        if (!doujinList.contains(doujin)) {
            if (doujin.hasFulfilledCriteria()) {
                doujinList.add(doujin)
            }
        }

        withContext(Dispatchers.Main) {
            checkItemSelections()
            searchResult.value = doujinList
        }
    }

    private suspend fun postResult(dir: File) {
        val isDuplicate = doujinList.any { doujin -> doujin.title == dir.name }
        if (!isDuplicate) {
            val doujin = dir.toDoujin() ?: return
            if (doujin.hasFulfilledCriteria()) {
                doujinList.add(doujin)
            }

            withContext(Dispatchers.Main) {
                checkItemSelections()
                searchResult.value = doujinList
            }
        }
    }

    // Used to check whether it contains title or path specified by criteria
    private fun Doujin.hasFulfilledCriteria(): Boolean {
        if (this.numberOfItems < minNumberPages || this.numberOfItems > maxNumberPages) {
            return false
        }

        for (keyword in titleKeywords) {
            val loweredCaseTitle = this.title.toLowerCase(Locale.ROOT)
            if (!loweredCaseTitle.contains(keyword)) {
                return false
            }
        }

        if (mustHaveDirs.isNotEmpty()) {
            if (this.parentDir !in mustHaveDirs) return false
        }

        return true
    }

    private fun checkItemSelections() {
        if (shouldResetSelections) {
            for (doujin in doujinList) {
                doujin.isSelected = false
            }
        } else {
            for (doujinItem in doujinList) {
                doujinItem.isSelected = doujinItem in selectedDoujins
            }
        }
        searchResult.value = doujinList
        shouldResetSelections = false
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
}