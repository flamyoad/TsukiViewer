package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.DoujinTagsDao
import com.flamyoad.tsukiviewer.db.dao.IncludedFolderDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedFolder
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import java.io.File

class SearchResultViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase
    val folderDao: IncludedFolderDao
    val doujinDetailsDao: DoujinDetailsDao
    val tagDao: TagDao
    val doujinTagDao: DoujinTagsDao

    val includedFolderList: LiveData<List<IncludedFolder>>

    val doujinList: LiveData<List<Doujin>>

    init {
        db = AppDatabase.getInstance(application)

        folderDao = db.includedFolderDao()
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()
        doujinTagDao = db.doujinTagDao()

        includedFolderList = folderDao.getAll()

        doujinList =
            Transformations.map(includedFolderList) { folders ->
                return@map fetchDoujinsFromDir(folders)
            }
    }

    private fun fetchDoujinsFromDir(includedFolders: List<IncludedFolder>): List<Doujin> {
        val doujinList = mutableListOf<Doujin>()
        for (folder in includedFolders) {
            walk(folder.dir, doujinList)
        }
        return doujinList
    }

    // Recursive method to search for directories & sub-directories
    // todo: this method doesnt include the main directory itself, fix it
    private fun walk(currentDir: File, doujinList: MutableList<Doujin>) {
        for (f in currentDir.listFiles()) {
            if (f.isDirectory) {
                val imageList = f.listFiles(ImageFileFilter()).sorted()

                if (imageList.isNotEmpty()) {
                    val coverImage = imageList.first().toUri()
                    val title = f.name
                    val numberOfImages = imageList.size
                    val lastModified = f.lastModified()

                    doujinList.add(
                        Doujin(coverImage, title, numberOfImages, lastModified, f)
                    )
                }

                walk(f, doujinList)
            }
        }
    }

}