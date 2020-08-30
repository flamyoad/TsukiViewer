package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.model.DoujinCollection

@Dao
interface DoujinCollectionDao {

    @Insert
    fun insert(collection: DoujinCollection)

    @Update
    fun update(collection: DoujinCollection)

    @Delete
    fun delete(collection: DoujinCollection)

    @Query("SELECT * FROM doujin_collection")
    fun getAll(collectionList: List<DoujinCollection>): LiveData<List<DoujinCollection>>

    @Query("SELECT * FROM doujin_collection")
    fun getAllBlocking(collectionList: List<DoujinCollection>): List<DoujinCollection>
}