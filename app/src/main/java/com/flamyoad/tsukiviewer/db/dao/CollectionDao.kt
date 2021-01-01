package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.model.Collection
import com.flamyoad.tsukiviewer.model.CollectionWithCriterias

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collection: Collection): Long

    @Delete
    suspend fun delete(collection: Collection)

    @Query("DELETE FROM collection WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM collection")
    fun getAll(): LiveData<List<Collection>>

    @Query("SELECT * FROM collection")
    suspend fun getAllBlocking(): List<Collection>

    @Query("SELECT * FROM collection")
    fun getAllWithCriterias(): LiveData<List<CollectionWithCriterias>>

    @Query("SELECT * FROM collection WHERE id = :collectionId")
    fun get(collectionId: Long): LiveData<Collection>

    @Query("SELECT * FROM collection WHERE id = :collectionId")
    suspend fun getBlocking(collectionId: Long): Collection
}