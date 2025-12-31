package com.flamyoad.tsukiviewer.core.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.core.model.Collection
import com.flamyoad.tsukiviewer.core.model.CollectionWithCriterias
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("""
        SELECT * FROM collection 
        WHERE name LIKE '%' || :keyword || '%'
    """)
    fun getAllWithCriterias(keyword: String): LiveData<List<CollectionWithCriterias>>

    @Query("SELECT * FROM collection WHERE id = :collectionId")
    fun get(collectionId: Long): LiveData<Collection>

    @Query("SELECT * FROM collection WHERE id = :collectionId")
    suspend fun getBlocking(collectionId: Long): Collection


    @Query("""
        UPDATE collection 
        SET coverPhoto = :thumbnail 
        WHERE id = :collectionId
        """)
    suspend fun updateThumbnail(collectionId: Long, thumbnail: File)

    @Query("UPDATE collection SET coverPhoto = '' WHERE id = :id ")
    suspend fun deleteThumbnail(id: Long)
}