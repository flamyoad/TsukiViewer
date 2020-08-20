package com.flamyoad.tsukiviewer.ui.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.model.EditorHistoryItem
import com.flamyoad.tsukiviewer.model.Tag

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase

    private val doujinDetailsDao: DoujinDetailsDao

    private val tagDao: TagDao

    val tagList: LiveData<List<Tag>>

    private val undoStack = mutableListOf<EditorHistoryItem>()

    lateinit var detailWithTags: LiveData<DoujinDetailsWithTags>

    init {
        db = AppDatabase.getInstance(application)
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()

        tagList = tagDao.getAll()
    }

    fun retrieveTags(dirPath: String) {
        detailWithTags = doujinDetailsDao.getLongDetailsByPath(dirPath)
    }

    fun pushUndo(item: EditorHistoryItem) {
        undoStack.add(item)
    }

    fun popUndo(): EditorHistoryItem? {
        if (undoStack.isEmpty()) {
            return null
        }

        val item = undoStack.last()
        undoStack.remove(item)

        return item
    }

}