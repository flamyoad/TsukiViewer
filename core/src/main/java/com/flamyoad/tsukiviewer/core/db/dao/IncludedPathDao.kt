package com.flamyoad.tsukiviewer.core.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.core.model.IncludedPath
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
interface IncludedPathDao {

    @Query("SELECT dir FROM included_path")
    fun getAll(): LiveData<List<IncludedPath>>

    @Query("SELECT dir FROM included_path")
    suspend fun getAllBlocking(): List<File>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(path: IncludedPath)

    @Delete
    suspend fun delete(path: IncludedPath)
}