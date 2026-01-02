package com.flamyoad.tsukiviewer.ui.screens.tags

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.db.dao.TagDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Tags screen.
 */
class TagsViewModel(
    private val application: Application,
    private val tagDao: TagDao
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TagsUiState())
    val uiState: StateFlow<TagsUiState> = _uiState.asStateFlow()
    
    init {
        loadTags()
    }
    
    fun loadTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val tags = tagDao.getAllBlocking()
                val tagUiModels = tags.map { tag ->
                    TagUiModel(
                        id = tag.tagId ?: 0L,
                        name = tag.name,
                        type = TagType.fromString(tag.type),
                        count = tag.count
                    )
                }
                
                _uiState.update { 
                    it.copy(
                        allTags = tagUiModels,
                        filteredTags = filterAndSortTags(tagUiModels, it.currentTagType, it.searchQuery, it.sortingMode),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        snackbarMessage = "Failed to load tags"
                    )
                }
            }
        }
    }
    
    fun setTabIndex(index: Int) {
        _uiState.update { state ->
            val newTagType = state.tabTypes.getOrElse(index) { TagType.ALL }
            state.copy(
                selectedTabIndex = index,
                filteredTags = filterAndSortTags(state.allTags, newTagType, state.searchQuery, state.sortingMode)
            )
        }
    }
    
    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredTags = filterAndSortTags(state.allTags, state.currentTagType, query, state.sortingMode)
            )
        }
    }
    
    fun setSearchActive(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
        if (!active) {
            setSearchQuery("")
        }
    }
    
    fun setSortingMode(mode: TagSortingMode) {
        _uiState.update { state ->
            state.copy(
                sortingMode = mode,
                filteredTags = filterAndSortTags(state.allTags, state.currentTagType, state.searchQuery, mode)
            )
        }
    }
    
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
    
    private fun filterAndSortTags(
        allTags: List<TagUiModel>,
        tagType: TagType,
        query: String,
        sortingMode: TagSortingMode
    ): List<TagUiModel> {
        return allTags
            .filter { tag ->
                (tagType == TagType.ALL || tag.type == tagType) &&
                (query.isBlank() || tag.name.contains(query, ignoreCase = true))
            }
            .let { filtered ->
                when (sortingMode) {
                    TagSortingMode.NAME_ASCENDING -> filtered.sortedBy { it.name }
                    TagSortingMode.NAME_DESCENDING -> filtered.sortedByDescending { it.name }
                    TagSortingMode.COUNT_ASCENDING -> filtered.sortedBy { it.count }
                    TagSortingMode.COUNT_DESCENDING -> filtered.sortedByDescending { it.count }
                }
            }
    }
}

/**
 * Factory for creating TagsViewModel instances.
 */
class TagsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagsViewModel::class.java)) {
            val db = AppDatabase.getInstance(application)
            return TagsViewModel(application, db.tagsDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
