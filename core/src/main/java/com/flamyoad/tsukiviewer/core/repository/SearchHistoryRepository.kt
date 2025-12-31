package com.flamyoad.tsukiviewer.core.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao
import com.flamyoad.tsukiviewer.core.model.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryRepository(
    val searchHistoryDao: SearchHistoryDao
) {

    fun getAll(pageSize: Int): LiveData<PagedList<SearchHistory>> {
        return searchHistoryDao.getAll()
            .toLiveData(pageSize)
    }

    suspend fun insertSearchHistory(item: SearchHistory) {
        withContext(Dispatchers.IO) {
            val lastInsertedItem = searchHistoryDao.getLatestItem()

            /* 1st condition: If last inserted item is null, means the search history has 0 items
               2nd condition: If user inputs the same thing as previous search,
                              then there is no need to insert into database
            */
            val shouldInsert = lastInsertedItem == null || !lastInsertedItem.sameWith(item)
            if (shouldInsert) {
                searchHistoryDao.insert(item)
            }
        }
    }

    suspend fun deleteSingle(item: SearchHistory) {
        withContext(Dispatchers.IO) {
            searchHistoryDao.delete(item)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            searchHistoryDao.deleteAll()
        }
    }
}