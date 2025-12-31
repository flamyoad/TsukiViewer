package com.flamyoad.tsukiviewer.core.repository

import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.core.db.dao.TagDao
import com.flamyoad.tsukiviewer.core.model.Tag

class TagRepository(
    private val tagDao: TagDao
) {

    fun getAll(): LiveData<List<Tag>> {
        return tagDao.getAll()
    }

    fun getAllWithFilter(keyword: String): LiveData<List<Tag>> {
        return tagDao.getAllWithFilter(keyword)
    }
}