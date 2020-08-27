package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.DoujinTagsDao
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class SearchResultViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase

    val pathDao: IncludedPathDao
    val doujinDetailsDao: DoujinDetailsDao
    val tagDao: TagDao
    val doujinTagDao: DoujinTagsDao

    private val imageExtensions = listOf("jpg", "png", "gif", "jpeg")

    private val includedPathList: LiveData<List<IncludedPath>>

    private val searchedResult = MutableLiveData<List<Doujin>>()

    private val isLoading = MutableLiveData<Boolean>(false)

    fun searchedResult(): LiveData<List<Doujin>> = searchedResult

    fun isLoading(): LiveData<Boolean> = isLoading

    init {
        db = AppDatabase.getInstance(application)

        pathDao = db.includedFolderDao()
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()
        doujinTagDao = db.doujinTagDao()

        includedPathList = pathDao.getAll()
    }

    suspend fun submitQuery(keyword: String, tags: String) {
        isLoading.value = true

        withContext(Dispatchers.IO) {
            val newList = mutableListOf<Doujin>()

            findFoldersFromDatabase(keyword, tags, newList)

            // Only search from directory instead of database
            // when user queries using keyword and did not specify tags
            if (tags.isBlank()) {
                findFoldersFromFileExplorer(keyword, newList)
            }
        }

        isLoading.value = false
    }

    private suspend fun findFoldersFromDatabase(keyword: String, tags: String, doujinList: MutableList<Doujin>) {
        // search with both title and tags
        if (keyword.isNotBlank() && tags.isNotBlank()) {
            val tagList = tags.split(",")
                .map { tagName -> tagName }

            val doujinDetailItems = doujinDetailsDao.findByTags(tagList, tagList.size)

            for (item in doujinDetailItems) {
                val containsKeywordEnglish = item.fullTitleEnglish.toLowerCase(Locale.ROOT).contains(keyword)
                val containsKeywordJap = item.fullTitleJapanese.contains(keyword)

                if (containsKeywordEnglish || containsKeywordJap) {
                    emitResult(item.absolutePath, doujinList)
                }
            }

        } else if (keyword.isNotBlank() && tags.isBlank()) {
//             search with title only
            val doujinDetailItems = doujinDetailsDao.findByTitle(keyword)

            for (item in doujinDetailItems) {
                emitResult(item.absolutePath, doujinList)
            }

        } else if (tags.isNotBlank() && keyword.isBlank()) {
            // search with tags only
            val tagList = tags.split(",")
                .map { tagName -> tagName }

            val doujinDetailItems = doujinDetailsDao.findByTags(tagList, tagList.size)

            for (item in doujinDetailItems) {
                emitResult(item.absolutePath, doujinList)
            }
        }
    }

    private suspend fun findFoldersFromFileExplorer(keyword: String, doujinList: MutableList<Doujin>) {
        val includedFolders = pathDao.getAllAsync()

        for (folder in includedFolders) {
            walk(folder.dir, folder.dir, keyword, doujinList)
        }
    }

    // Recursive method to search for directories & sub-directories
    private suspend fun walk(current: File, parentDir: File, keyword: String, doujinList: MutableList<Doujin>) {
        if (current.isDirectory) {
            val fileList = current.listFiles()

            val containsKeyword = current
                .name
                .toLowerCase(Locale.ROOT)
                .contains(keyword)

            if (containsKeyword) {
                val imageList = fileList.filter { f -> f.extension in imageExtensions }

                if (imageList.isNotEmpty()) {

                    val coverImage = imageList.first().toUri()
                    val title = current.name
                    val numberOfImages = imageList.size
                    val lastModified = current.lastModified()

                    val doujin = Doujin(coverImage, title, numberOfImages, lastModified, current)
                    emitResult(doujin, doujinList)
                }
            }

            for (f in fileList) {
                walk(f, parentDir, keyword, doujinList)
            }
        }
    }

    private fun emitResult(dir: File, doujinList: MutableList<Doujin>) {
        val isDuplicate = doujinList.any { doujin -> doujin.title == dir.name}
        if (!isDuplicate) {
            doujinList.add(dir.toDoujin())
            searchedResult.postValue(doujinList)
        }
    }

    private fun emitResult(doujin: Doujin, doujinList: MutableList<Doujin>) {
        if (!doujinList.contains(doujin)) {
            doujinList.add(doujin)
            searchedResult.postValue(doujinList)
        }
    }

    fun File.toDoujin(): Doujin {
        val imageList = this.listFiles(ImageFileFilter())

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