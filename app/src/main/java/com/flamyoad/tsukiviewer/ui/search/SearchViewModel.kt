package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.Tag

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase

    private val tagDao: TagDao

    val tagList: LiveData<List<Tag>>

    init {
        db = AppDatabase.getInstance(application)
        tagDao = db.tagsDao()

        tagList = tagDao.getAll()
    }
}