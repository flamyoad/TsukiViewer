package com.flamyoad.tsukiviewer.ui.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase

    private val doujinDetailsDao: DoujinDetailsDao

    lateinit var detailWithTags: LiveData<DoujinDetailsWithTags>

    init {
        db = AppDatabase.getInstance(application)
        doujinDetailsDao = db.doujinDetailsDao()
    }

    fun retrieveTags(dirPath: String) {
        detailWithTags = doujinDetailsDao.getLongDetailsByPath(dirPath)
    }

}