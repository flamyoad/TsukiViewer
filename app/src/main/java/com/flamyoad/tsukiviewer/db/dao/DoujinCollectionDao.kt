package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.model.DoujinCollection

@Dao
interface DoujinCollectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collection: DoujinCollection)

    @Update
    suspend fun update(collection: DoujinCollection)

    @Delete
    suspend fun delete(collection: DoujinCollection)

    @Query("SELECT * FROM doujin_collection")
    fun getAll(): LiveData<List<DoujinCollection>>

    @Query("SELECT * FROM doujin_collection")
    fun getAllBlocking(): List<DoujinCollection>
}