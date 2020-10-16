package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.flamyoad.tsukiviewer.model.SearchHistory

@Dao
interface SearchHistoryDao {
    @Insert
    fun insert(searchHistory: SearchHistory)

    @Delete
    fun delete(searchHistory: SearchHistory): Int

    @Query("SELECT * FROM search_history")
    fun getAll(): DataSource.Factory<Int, SearchHistory>
}