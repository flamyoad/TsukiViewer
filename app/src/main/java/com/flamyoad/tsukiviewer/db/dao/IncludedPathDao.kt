package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.flamyoad.tsukiviewer.model.IncludedPath

@Dao
interface IncludedPathDao {

    @Query("SELECT * FROM included_path")
    fun getAll(): LiveData<List<IncludedPath>>

    @Query("SELECT * FROM included_path")
    suspend fun getAllBlocking(): List<IncludedPath>

    @Insert
    suspend fun insert(path: IncludedPath)

    @Delete
    suspend fun delete(path: IncludedPath)
}