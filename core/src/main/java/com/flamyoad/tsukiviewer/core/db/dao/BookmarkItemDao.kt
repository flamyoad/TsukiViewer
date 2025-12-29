package com.flamyoad.tsukiviewer.core.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.core.model.BookmarkItem
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
interface BookmarkItemDao {

    @Query("SELECT * FROM bookmark_item")
    fun selectAll(): LiveData<List<BookmarkItem>>

    @Query("SELECT * FROM bookmark_item WHERE parentName = :groupName")
    fun from(groupName: String): LiveData<List<BookmarkItem>>

    @Query("SELECT * FROM bookmark_item WHERE parentName = :groupName")
    suspend fun selectFrom(groupName: String): List<BookmarkItem>

    @Query("""
        SELECT EXISTS(SELECT * FROM bookmark_item 
                      WHERE parentName = :groupName AND absolutePath = :folderPath)
    """)
    suspend fun exists(folderPath: File, groupName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BookmarkItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(items: List<BookmarkItem>): List<Long>

    @Delete
    suspend fun delete(item: BookmarkItem)

    @Delete
    suspend fun delete(items: List<BookmarkItem>): Int

    @Query("DELETE FROM bookmark_item WHERE absolutePath = :absolutePath AND parentName = :groupName")
    suspend fun delete(absolutePath: File, groupName: String): Int

    @Query("DELETE FROM bookmark_item WHERE absolutePath = :path")
    suspend fun deleteFromAllGroups(path: File): Int
}