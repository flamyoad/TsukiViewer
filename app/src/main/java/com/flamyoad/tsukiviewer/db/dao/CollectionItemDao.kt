package com.flamyoad.tsukiviewer.db.dao

import androidx.room.Delete
import androidx.room.Insert
import com.flamyoad.tsukiviewer.model.CollectionItem

interface CollectionItemDao {

    @Insert
    fun insert(item: CollectionItem)

    @Delete
    fun delete(item: CollectionItem)
}