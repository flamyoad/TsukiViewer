package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.model.SearchHistory
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.SearchHistoryRepository
import com.flamyoad.tsukiviewer.repository.TagRepository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getInstance(application)

    private val searchHistoryRepo = SearchHistoryRepository(application.applicationContext)
    private val tagRepo = TagRepository(application.applicationContext)

    private val tagQuery = MutableLiveData<String>("")

    val tagList: LiveData<List<Tag>>

    val searchHistories: LiveData<PagedList<SearchHistory>>

    init {
        searchHistories = searchHistoryRepo.getAll(pageSize = 8)

        tagList = Transformations.switchMap(tagQuery) {
            return@switchMap tagRepo.getAllWithFilter(it)
        }
    }

    fun setQuery(query: String) {
        tagQuery.value = query
    }

    fun clearQuery() {
        tagQuery.value = ""
    }

    fun insertSearchHistory(item: SearchHistory) {
        viewModelScope.launch {
            searchHistoryRepo.insertSearchHistory(item)
        }
    }

    fun deleteSearchHistory(item: SearchHistory) {
        viewModelScope.launch {
            searchHistoryRepo.deleteSingle(item)
        }
    }
}