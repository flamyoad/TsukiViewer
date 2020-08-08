package com.flamyoad.tsukiviewer.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.flamyoad.tsukiviewer.model.DoujinTag

@Dao
interface DoujinTagsDao {

    @Insert
    fun insert(doujinTag: DoujinTag)
}