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
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*


class SearchResultViewModel(private val app: Application) : AndroidViewModel(app) {
    private val context: Context

    private val contentResolver: ContentResolver

    private val db: AppDatabase

    val pathDao: IncludedPathDao
    val doujinDetailsDao: DoujinDetailsDao
    val tagDao: TagDao
    val doujinTagDao: DoujinTagsDao

    private val includedPathList: LiveData<List<IncludedPath>>

    // DO not modify this list in any other places than the coroutine started in submitQuery() function
    private val doujinList = mutableListOf<Doujin>()

    private val searchResult = MutableLiveData<List<Doujin>>()

    private val isLoading = MutableLiveData<Boolean>(false)

    private var filterJob: Job? = null

    fun searchedResult(): LiveData<List<Doujin>> = searchResult

    fun isLoading(): LiveData<Boolean> = isLoading

    init {
        db = AppDatabase.getInstance(app)

        context = app.applicationContext

        contentResolver = app.contentResolver

        pathDao = db.includedFolderDao()
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()
        doujinTagDao = db.doujinTagDao()

        includedPathList = pathDao.getAll()
    }

    suspend fun submitQuery(keyword: String, tags: String) {
        isLoading.value = true

        withContext(Dispatchers.IO) {

            searchFromDatabase(keyword, tags)

            // If tags are not specified in the query, then we have to search from file explorer too
            if (tags.isBlank()) {
                val existingList = (app as MyApplication).fullDoujinList
                if (existingList != null) {
                    searchFromExistingList(existingList, keyword)
                } else {
                    searchFromFileExplorer(keyword)
                }
            }
        }

        isLoading.value = false
    }

    private suspend fun searchFromDatabase(keyword: String, tags: String) {
        if (keyword.isNotBlank() && tags.isNotBlank()) {
            // Search using both title and tags
            val tagList = tags.split(",")
                .map { tagName -> tagName }

            val doujinDetailItems = doujinDetailsDao.findByTags(tagList, tagList.size)

            for (item in doujinDetailItems) {
                val containsKeywordEnglish = item.fullTitleEnglish.toLowerCase(Locale.ROOT).contains(keyword)
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

            val doujinDetailItems = doujinDetailsDao.findByTags(tagList, tagList.size)

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
                "%" + keyword + "%")

            val cursor = ContentResolverCompat.query(contentResolver,
                uri,
                projection,
                selection,
                params,
                null,
                null)

            while (cursor.moveToNext()) {
                val idSet = mutableSetOf<String>()

                val fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val parentId = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT))

                if (idSet.add(parentId)) {
                    val dir = File(fullPath)

                    val imageList = dir.listFiles(ImageFileFilter())

                    if (!imageList.isNullOrEmpty()) {
                        val doujin = Doujin(
                            pic = imageList.first().toUri(),
                            title = dir.name,
                            path = dir,
                            lastModified = dir.lastModified(),
                            numberOfItems = imageList.size
                        )
                        postResult(doujin)
                    }
                }
                Log.d("cursor", "Full Path: ${fullPath}, Parent index: ${parentId}")
            }
        }
    }

    private fun searchFromExistingList(doujinList: List<Doujin>, keyword: String) {
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

    fun getImages(id: String): List<File> {
        val uri = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )

        val selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" +
                "AND " +
                MediaStore.Files.FileColumns.PARENT + " = ?"

        val selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), id)

        val cursor = ContentResolverCompat.query(
            contentResolver,
            uri,
            projection,
            selection,
            selectionArgs,
            " ${MediaStore.Files.FileColumns.TITLE}",
            null
        )

        val imageList = mutableListOf<File>()
        while (cursor.moveToNext()) {
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            imageList.add(File(imagePath))
        }
        return imageList
    }

    fun getImageCount(id: Long): Int {
        val selection = ("( "
                + MediaStore.Files.FileColumns.MEDIA_TYPE
                + "=? ) and "
                + MediaStore.Files.FileColumns.PARENT
                + "=?")

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), java.lang.String.valueOf(id)
        )

        val cur = ContentResolverCompat
            .query(
                contentResolver,
                MediaStore.Files.getContentUri("external"),
                arrayOf(MediaStore.Files.FileColumns.PARENT),
                selection,
                selectionArgs,
                null,
                null
            )
        return cur.count
    }

    private fun postResult(doujin: Doujin) {
        if (!doujinList.contains(doujin)) {
            doujinList.add(doujin)
            searchResult.postValue(doujinList)
        }
    }

    private fun postResult(dir: File) {
        val isDuplicate = doujinList.any { doujin -> doujin.title == dir.name}
        if (!isDuplicate) {
            val doujin = dir.toDoujin()
            if (doujin != null) {
                doujinList.add(doujin)
                searchResult.postValue(doujinList)
            }
        }
    }

    fun File.toDoujin(): Doujin? {
        val imageList = this.listFiles(ImageFileFilter())

        if (imageList == null) {
            return null
        }

        val doujin = Doujin(
            pic = imageList.first().toUri(),
            title = this.name,
            path = this,
            lastModified = this.lastModified(),
            numberOfItems = imageList.size
        )
        return doujin
    }
}