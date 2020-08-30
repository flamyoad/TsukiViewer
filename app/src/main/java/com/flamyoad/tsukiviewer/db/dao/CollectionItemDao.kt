package com.flamyoad.tsukiviewer.db.dao

import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.model.CollectionItem
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
interface CollectionItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: CollectionItem)

    @Delete
    suspend fun delete(item: CollectionItem)

    @Query("DELETE FROM collection_item WHERE absolutePath = :path")
    suspend fun deleteFromAllCollections(path: File)
}