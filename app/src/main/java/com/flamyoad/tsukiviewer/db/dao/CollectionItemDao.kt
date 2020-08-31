package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.model.CollectionItem
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
interface CollectionItemDao {

    @Query("SELECT * FROM collection_item")
    fun selectAll(): LiveData<List<CollectionItem>>

    @Query("SELECT * FROM collection_item WHERE collectionName = :collectionName")
    suspend fun selectFrom(collectionName: String): List<CollectionItem>

    @Insert
    suspend fun insert(item: CollectionItem)

    @Delete
    suspend fun delete(item: CollectionItem)

    @Query("DELETE FROM collection_item WHERE absolutePath = :path")
    suspend fun deleteFromAllCollections(path: File)
}