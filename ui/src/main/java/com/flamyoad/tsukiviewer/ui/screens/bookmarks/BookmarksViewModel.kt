package com.flamyoad.tsukiviewer.ui.screens.bookmarks

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for the Bookmarks screen.
 */
class BookmarksViewModel(
    private val application: Application,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()
    
    private val selectedItems = mutableListOf<BookmarkItemUiModel>()
    private var allItems: List<BookmarkItemUiModel> = emptyList()
    
    init {
        loadBookmarks()
    }
    
    fun loadBookmarks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val groups = bookmarkRepository.getAllGroupsBlocking()
                val groupUiModels = groups.map { group ->
                    BookmarkGroupUiModel(name = group.name)
                }
                
                _uiState.update { 
                    it.copy(
                        groups = groupUiModels,
                        selectedGroupName = groupUiModels.firstOrNull()?.name,
                        isLoading = false
                    )
                }
                
                // Load items for the first group
                groupUiModels.firstOrNull()?.let { group ->
                    loadItemsForGroup(group.name)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        snackbarMessage = "Failed to load bookmarks"
                    )
                }
            }
        }
    }
    
    fun selectGroup(groupName: String) {
        _uiState.update { it.copy(selectedGroupName = groupName) }
        loadItemsForGroup(groupName)
    }
    
    private fun loadItemsForGroup(groupName: String) {
        viewModelScope.launch {
            try {
                val items = bookmarkRepository.getAllItemsFrom(groupName)
                allItems = items.mapNotNull { item ->
                    val coverImage = findCoverImage(item.absolutePath)
                    BookmarkItemUiModel(
                        id = item.id ?: 0L,
                        title = item.absolutePath.name,
                        absolutePath = item.absolutePath.absolutePath,
                        coverImageUri = coverImage?.let { Uri.fromFile(it) },
                        isSelected = false
                    )
                }
                _uiState.update { it.copy(items = allItems) }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Failed to load items") }
            }
        }
    }
    
    private fun findCoverImage(directory: File): File? {
        if (!directory.exists() || !directory.isDirectory) return null
        
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "webp")
        val files = directory.listFiles { file ->
            file.isFile && imageExtensions.any { ext -> 
                file.extension.equals(ext, ignoreCase = true) 
            }
        }?.sortedBy { it.name }
        
        return files?.firstOrNull()
    }
    
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterItems(query)
    }
    
    private fun filterItems(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(items = allItems) }
        } else {
            val filtered = allItems.filter { item ->
                item.title.contains(query, ignoreCase = true)
            }
            _uiState.update { it.copy(items = filtered) }
        }
    }
    
    fun setSearchActive(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
        if (!active) {
            setSearchQuery("")
        }
    }
    
    fun onItemClick(item: BookmarkItemUiModel) {
        if (_uiState.value.isActionModeActive) {
            toggleItemSelection(item)
        }
    }
    
    fun onItemLongClick(item: BookmarkItemUiModel) {
        if (!_uiState.value.isActionModeActive) {
            startActionMode()
        }
        toggleItemSelection(item)
    }
    
    fun startActionMode() {
        _uiState.update { it.copy(isActionModeActive = true) }
    }
    
    fun cancelActionMode() {
        selectedItems.clear()
        updateSelectionState()
        _uiState.update { 
            it.copy(
                isActionModeActive = false,
                selectedCount = 0
            )
        }
    }
    
    private fun toggleItemSelection(item: BookmarkItemUiModel) {
        val isSelected = selectedItems.any { it.id == item.id }
        if (isSelected) {
            selectedItems.removeAll { it.id == item.id }
        } else {
            selectedItems.add(item)
        }
        
        if (selectedItems.isEmpty()) {
            cancelActionMode()
        } else {
            updateSelectionState()
            _uiState.update { it.copy(selectedCount = selectedItems.size) }
        }
    }
    
    private fun updateSelectionState() {
        val selectedIds = selectedItems.map { it.id }.toSet()
        _uiState.update { state ->
            state.copy(
                items = state.items.map { item ->
                    item.copy(isSelected = item.id in selectedIds)
                }
            )
        }
    }
    
    fun deleteSelectedItems() {
        viewModelScope.launch {
            try {
                val selectedIds = selectedItems.map { it.id }.toSet()
                val currentGroup = _uiState.value.selectedGroupName ?: return@launch
                
                // Delete from repository using path and group
                for (item in selectedItems) {
                    bookmarkRepository.itemDao.delete(File(item.absolutePath), currentGroup)
                }
                
                allItems = allItems.filter { it.id !in selectedIds }
                _uiState.update { state ->
                    state.copy(
                        items = state.items.filter { it.id !in selectedIds },
                        snackbarMessage = "${selectedIds.size} items deleted"
                    )
                }
                cancelActionMode()
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Failed to delete items") }
            }
        }
    }
    
    fun showCreateGroupDialog() {
        _uiState.update { it.copy(showCreateGroupDialog = true) }
    }
    
    fun hideCreateGroupDialog() {
        _uiState.update { it.copy(showCreateGroupDialog = false) }
    }
    
    fun createNewGroup(name: String) {
        viewModelScope.launch {
            try {
                val newGroup = BookmarkGroup(name = name)
                bookmarkRepository.insertGroup(newGroup)
                
                val newGroupUiModel = BookmarkGroupUiModel(name = name)
                _uiState.update { state ->
                    state.copy(
                        groups = state.groups + newGroupUiModel,
                        snackbarMessage = "Group '$name' created",
                        showCreateGroupDialog = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Failed to create group") }
            }
        }
    }
    
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

/**
 * Factory for creating BookmarksViewModel instances.
 */
class BookmarksViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarksViewModel::class.java)) {
            val db = AppDatabase.getInstance(application)
            val bookmarkRepository = BookmarkRepository(
                db = db,
                groupDao = db.bookmarkGroupDao(),
                itemDao = db.bookmarkItemDao()
            )
            return BookmarksViewModel(application, bookmarkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
