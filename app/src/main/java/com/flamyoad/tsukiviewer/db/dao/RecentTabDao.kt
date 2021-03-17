package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.model.RecentTab

@Dao
@TypeConverters(FolderConverter::class)
interface RecentTabDao {

    @Query("SELECT * FROM recent_tabs")
    fun getAll(): LiveData<List<RecentTab>>

    @Query("SELECT * FROM recent_tabs WHERE id = :id")
    suspend fun get(id: Long): RecentTab

    @Query("SELECT * FROM recent_tabs WHERE dirPath = :path")
    suspend fun getByPath(path: String): RecentTab?

    @Query("SELECT EXISTS (SELECT * FROM recent_tabs WHERE dirPath = :path)")
    fun existsByPath(path: String): Boolean

    @Insert
    fun insert(tab: RecentTab): Long

    @Delete
    fun delete(tab: RecentTab)
}