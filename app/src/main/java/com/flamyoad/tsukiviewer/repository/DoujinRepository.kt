package com.flamyoad.tsukiviewer.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import androidx.core.net.toUri
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import com.flamyoad.tsukiviewer.utils.extensions.toDoujin
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.*

class DoujinRepository(private val context: Context) {
    private val contentResolver: ContentResolver = context.contentResolver
    private val db: AppDatabase = AppDatabase.getInstance(context)
    private val doujinDetailsDao: DoujinDetailsDao = db.doujinDetailsDao()
    private val pathDao: IncludedPathDao = db.includedFolderDao()
    private val existingList = (context.applicationContext as MyApplication).fullDoujinList

    fun scanForDoujins(keyword: String, tags: String, shouldIncludeAllTags: Boolean)
            : Flow<Doujin> {
        val keywordLowerCase = keyword.toLowerCase(Locale.ROOT)

        val flowFromDatabase = searchFromDatabase(keywordLowerCase, tags, shouldIncludeAllTags)
        val flowFromFileExplorer: Flow<Doujin> = if (tags.isBlank()) {
            if (existingList != null) {
                searchFromExistingList(keywordLowerCase)
            } else {
                searchFromFileExplorer(keywordLowerCase)
            }
        } else {
            emptyFlow()
        }

        return flowOf(flowFromDatabase, flowFromFileExplorer).flattenMerge()
    }

    private fun searchFromDatabase(keyword: String, tags: String, shouldIncludeAllTags: Boolean)
            : Flow<Doujin> = flow {

        if (keyword.isNotBlank() && tags.isNotBlank()) { // Search using both title and tags
            val tagList = tags.split(",")
                .map { tagName -> tagName }

            val doujinDetailItems = when (shouldIncludeAllTags) {
                true -> doujinDetailsDao.findByTags(tagList, tagList.size) // Searched items must include all tags
                false -> doujinDetailsDao.findByTags(tagList) // Searched items must include at least 1 tag
            }

            for (item in doujinDetailItems) {
                val containsKeywordEnglish = item.fullTitleEnglish.toLowerCase(Locale.ROOT).contains(keyword)
                val containsKeywordJap = item.fullTitleJapanese.contains(keyword)

                if (containsKeywordEnglish || containsKeywordJap) {
                    emit(item.absolutePath.toDoujin() ?: return@flow)
                }
            }

        } else if (keyword.isNotBlank() && tags.isBlank()) { // Search using title only
            val doujinDetailItems = doujinDetailsDao.findByTitle(keyword)

            for (item in doujinDetailItems) {
                emit(item.absolutePath.toDoujin() ?: return@flow)
            }

        } else if (tags.isNotBlank() && keyword.isBlank()) { // Search using tags only
            val tagList = tags.split(",")
                .map { tagName -> tagName }

            val doujinDetailItems = when (shouldIncludeAllTags) {
                true -> doujinDetailsDao.findByTags(tagList, tagList.size)
                false -> doujinDetailsDao.findByTags(tagList)
            }

            for (item in doujinDetailItems) {
                emit(item.absolutePath.toDoujin() ?: return@flow)
            }
        }
    }

    private fun searchFromFileExplorer(keyword: String): Flow<Doujin> = flow {
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
                        emit(doujin)
                    }
                }
            }
        }
    }

    private fun searchFromExistingList(keyword: String): Flow<Doujin> = flow {
        if (existingList == null) {
            return@flow
        }

        val newList = mutableListOf<Doujin>()
        for (doujin in existingList) {
            if (doujin.title.toLowerCase(Locale.ROOT).contains(keyword)) {
                newList.add(doujin)
                emit(doujin)
            }
        }
    }

    private fun MutableList<Doujin>.addIfNotNull(doujinDetails: DoujinDetails) {
        val doujin = doujinDetails.absolutePath.toDoujin()
        if (doujin != null) {
            this.add(doujin)
        }
    }
}