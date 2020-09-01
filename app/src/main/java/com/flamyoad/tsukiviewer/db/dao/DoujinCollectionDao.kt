package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.model.DoujinCollection
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
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
    suspend fun getAllBlocking(): List<DoujinCollection>

    @Query("SELECT collectionName FROM collection_item WHERE absolutePath = :absolutePath")
    suspend fun getCollectionNamesFrom(absolutePath: File): List<String>
}