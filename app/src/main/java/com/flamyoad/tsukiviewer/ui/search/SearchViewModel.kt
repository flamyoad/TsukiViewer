package com.flamyoad.tsukiviewer.ui.search

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.flamyoad.tsukiviewer.core.model.SearchHistory
import com.flamyoad.tsukiviewer.core.model.Tag
import com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository
import com.flamyoad.tsukiviewer.core.repository.TagRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchHistoryRepo: SearchHistoryRepository,
    private val tagRepo: TagRepository
) : ViewModel() {

    private val tagQuery = MutableLiveData<String>("")

    val tagList: LiveData<List<Tag>>

    val searchHistories: LiveData<PagedList<SearchHistory>>

    init {
        searchHistories = searchHistoryRepo.getAll(pageSize = 8)

        tagList = tagQuery.switchMap {
            tagRepo.getAllWithFilter(it)
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