package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.SearchHistoryDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.SearchHistory
import com.flamyoad.tsukiviewer.model.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getInstance(application)

    private val tagDao: TagDao

    private val searchHistoryDao: SearchHistoryDao

    private val tagQuery = MutableLiveData<String>("")

    val tagList: LiveData<List<Tag>>

    val searchHistories: LiveData<PagedList<SearchHistory>>

    init {
        tagDao = db.tagsDao()
        searchHistoryDao = db.searchHistoryDao()

        searchHistories = searchHistoryDao.getAll()
            .toLiveData(pageSize = 4)

        tagList = Transformations.switchMap(tagQuery) {
            return@switchMap tagDao.getAllWithFilter(it)
        }
    }

    fun setQuery(query: String) {
        tagQuery.value = query
    }

    fun clearQuery() {
        tagQuery.value = ""
    }

    fun insertSearchHistory(item: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun deleteSearchHistory(item: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryDao.delete(item)
        }
    }
}