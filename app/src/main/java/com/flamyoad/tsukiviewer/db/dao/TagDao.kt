package com.flamyoad.tsukiviewer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.flamyoad.tsukiviewer.model.Tag

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: Tag): Long

    @Query("SELECT EXISTS(SELECT * FROM tags WHERE type = :type AND name = :name)")
    suspend fun exists(type: String, name: String): Boolean

    @Query("SELECT tagId from tags WHERE type = :type AND name = :name")
    suspend fun getId(type: String, name: String): Long

    @Query("UPDATE tags SET count = count + 1 WHERE type = :type AND name = :name")
    suspend fun incrementCount(type: String, name: String)

    // suspend fun removeTag(doujin, tag)
}