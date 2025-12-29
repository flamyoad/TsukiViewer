package com.flamyoad.tsukiviewer.core.repository

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import androidx.core.net.toUri
import com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.core.model.Doujin
import com.flamyoad.tsukiviewer.core.model.DoujinDetails
import com.flamyoad.tsukiviewer.core.utils.ImageFileFilter
import com.flamyoad.tsukiviewer.core.utils.extensions.toDoujin
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoujinRepository @Inject constructor(
    private val application: Application,
    private val doujinDetailsDao: DoujinDetailsDao,
    private val pathDao: IncludedPathDao
) {
    private val contentResolver: ContentResolver = application.contentResolver
    
    // Cache for the full doujin list - can be set from outside
    var cachedDoujinList: MutableList<Doujin>? = null

    fun scanForDoujins(keyword: String, tags: String, shouldIncludeAllTags: Boolean)
            : Flow<Doujin> {
        val keywordLowerCase = keyword.toLowerCase(Locale.ROOT)

        val flowFromDatabase = searchFromDatabase(keywordLowerCase, tags, shouldIncludeAllTags)
        val flowFromFileExplorer: Flow<Doujin> = if (tags.isBlank()) {
            if (cachedDoujinList != null) {
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
        val list = cachedDoujinList ?: return@flow

        val newList = mutableListOf<Doujin>()
        for (doujin in list) {
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