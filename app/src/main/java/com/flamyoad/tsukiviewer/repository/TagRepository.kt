package com.flamyoad.tsukiviewer.repository

import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.Tag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val tagDao: TagDao
) {

    fun getAll(): LiveData<List<Tag>> {
        return tagDao.getAll()
    }

    fun getAllWithFilter(keyword: String): LiveData<List<Tag>> {
        return tagDao.getAllWithFilter(keyword)
    }
}