package com.flamyoad.tsukiviewer.ui.screens.collections

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.repository.CollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for the Collections screen.
 */
class CollectionsViewModel(
    private val application: Application,
    private val collectionRepository: CollectionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()
    
    private var allCollections: List<CollectionUiModel> = emptyList()
    
    init {
        loadCollections()
    }
    
    fun loadCollections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val collections = collectionRepository.getAll()
                
                allCollections = collections.map { collection ->
                    val coverFile = collection.coverPhoto
                    val coverUri = if (coverFile.exists()) {
                        Uri.fromFile(coverFile)
                    } else {
                        null
                    }
                    
                    CollectionUiModel(
                        id = collection.id ?: 0L,
                        title = collection.name,
                        coverImageUri = coverUri,
                        itemCount = 0, // Will need criteria count
                        tags = emptyList()
                    )
                }
                
                _uiState.update { 
                    it.copy(
                        collections = allCollections,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        snackbarMessage = "Failed to load collections"
                    )
                }
            }
        }
    }
    
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterCollections(query)
    }
    
    private fun filterCollections(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(collections = allCollections) }
        } else {
            val filtered = allCollections.filter { collection ->
                collection.title.contains(query, ignoreCase = true) ||
                collection.tags.any { it.contains(query, ignoreCase = true) }
            }
            _uiState.update { it.copy(collections = filtered) }
        }
    }
    
    fun setViewStyle(style: CollectionViewStyle) {
        _uiState.update { it.copy(viewStyle = style) }
    }
    
    fun setSearchActive(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
        if (!active) {
            setSearchQuery("")
        }
    }
    
    fun showEditDialog(collectionId: Long) {
        // For now, show a snackbar indicating the action
        _uiState.update { it.copy(snackbarMessage = "Edit collection: $collectionId") }
    }
    
    fun showInfoDialog(collectionId: Long) {
        viewModelScope.launch {
            val collection = allCollections.find { it.id == collectionId }
            collection?.let {
                val info = "Collection: ${it.title}\nItems: ${it.itemCount}\nTags: ${it.tags.joinToString(", ")}"
                _uiState.update { state -> state.copy(snackbarMessage = info) }
            }
        }
    }
    
    fun deleteCollection(collectionId: Long) {
        viewModelScope.launch {
            try {
                collectionRepository.delete(collectionId)
                allCollections = allCollections.filter { it.id != collectionId }
                _uiState.update { state ->
                    state.copy(
                        collections = state.collections.filter { it.id != collectionId },
                        snackbarMessage = "Collection deleted"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Failed to delete collection") }
            }
        }
    }
    
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

/**
 * Factory for creating CollectionsViewModel instances.
 */
class CollectionsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionsViewModel::class.java)) {
            val db = AppDatabase.getInstance(application)
            val collectionRepository = CollectionRepository(
                db = db,
                collectionDao = db.collectionDao(),
                criteriaDao = db.collectionCriteriaDao(),
                collectionDoujinDao = db.collectionDoujinDao()
            )
            return CollectionsViewModel(application, collectionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
