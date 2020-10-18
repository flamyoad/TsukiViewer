package com.flamyoad.tsukiviewer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.model.Tag

class TagRepository(private val context: Context) {
    private val db: AppDatabase = AppDatabase.getInstance(context)
    private val tagDao = db.tagsDao()

    fun getAll(): LiveData<List<Tag>> {
        return tagDao.getAll()
    }

    fun getAllWithFilter(keyword: String): LiveData<List<Tag>> {
        return tagDao.getAllWithFilter(keyword)
    }
}