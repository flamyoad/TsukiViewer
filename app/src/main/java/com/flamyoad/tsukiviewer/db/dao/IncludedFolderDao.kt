package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.tsukiviewer.model.IncludedFolder

@Dao
interface IncludedFolderDao {

    @Query("SELECT * FROM included_folders")
    fun getAll(): LiveData<List<IncludedFolder>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(folder: IncludedFolder)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(list: List<IncludedFolder>)
}