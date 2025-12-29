package com.flamyoad.tsukiviewer.core.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
interface BookmarkGroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collection: BookmarkGroup)

    @Update
    suspend fun update(collection: BookmarkGroup)

    @Delete
    suspend fun delete(collection: BookmarkGroup)

    @Query("DELETE FROM bookmark_group WHERE name = :collectionName")
    suspend fun delete(collectionName: String)

    @Query("""
        UPDATE bookmark_group 
        SET name = :newName 
        WHERE name = :oldName
        """)
    suspend fun changeName(oldName: String, newName: String)

    @Query("SELECT * FROM bookmark_group WHERE name = :name")
    fun get(name: String): LiveData<BookmarkGroup>

    @Query("SELECT * FROM bookmark_group")
    fun getAll(): LiveData<List<BookmarkGroup>>

    @Query("SELECT EXISTS(SELECT * FROM bookmark_group WHERE name = :name)")
    fun exists(name: String): LiveData<Boolean>

    @Query("SELECT * FROM bookmark_group")
    suspend fun getAllBlocking(): List<BookmarkGroup>

    @Query("SELECT parentName FROM bookmark_item WHERE absolutePath = :absolutePath")
    suspend fun getCollectionNamesFrom(absolutePath: File): List<String>
}