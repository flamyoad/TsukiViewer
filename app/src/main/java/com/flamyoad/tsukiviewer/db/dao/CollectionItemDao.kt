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

    @Query("""
        SELECT EXISTS(SELECT * FROM collection_item 
                      WHERE collectionName = :collectionName AND absolutePath = :folderPath)
    """)
    suspend fun exists(folderPath: File, collectionName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: CollectionItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(items: List<CollectionItem>): List<Long>

    @Delete
    suspend fun delete(item: CollectionItem)

    @Delete
    suspend fun delete(items: List<CollectionItem>): Int

    @Query("DELETE FROM collection_item WHERE absolutePath = :absolutePath AND collectionName = :collectionName")
    suspend fun delete(absolutePath: File, collectionName: String): Int

    @Query("DELETE FROM collection_item WHERE absolutePath = :path")
    suspend fun deleteFromAllCollections(path: File): Int
}