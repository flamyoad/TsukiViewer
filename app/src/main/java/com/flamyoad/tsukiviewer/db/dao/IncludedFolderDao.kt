package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.flamyoad.tsukiviewer.model.IncludedFolder

@Dao
interface IncludedFolderDao {

    @Query("SELECT * FROM included_folders")
    fun getAll(): LiveData<List<IncludedFolder>>

    @Query("SELECT * FROM included_folders")
    suspend fun getAllBlocking(): List<IncludedFolder>

    @Insert
    suspend fun insert(folder: IncludedFolder)

    @Delete
    suspend fun delete(folder: IncludedFolder)
}