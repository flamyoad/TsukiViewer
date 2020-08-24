package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.tsukiviewer.model.Tag

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: Tag): Long

    @Query("SELECT * FROM tags ORDER BY name")
    fun getAll(): LiveData<List<Tag>>

    @Query("SELECT * FROM tags WHERE type = :category ORDER BY name")
    fun getByCategory(category: String): LiveData<List<Tag>>

    @Query("SELECT * FROM tags WHERE type = :type AND name = :name")
    suspend fun get(type: String, name: String): Tag?

    @Query("SELECT EXISTS(SELECT * FROM tags WHERE type = :type AND name = :name)")
    suspend fun exists(type: String, name: String): Boolean

    @Query("SELECT tagId from tags WHERE type = :type AND name = :name")
    suspend fun getId(type: String, name: String): Long

    @Query("UPDATE tags SET count = count + 1 WHERE type = :type AND name = :name")
    suspend fun incrementCount(type: String, name: String)

    @Query("UPDATE tags SET count = count - 1 WHERE type = :type AND name = :name")
    suspend fun decrementCount(type: String, name: String)

}