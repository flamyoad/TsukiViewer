package com.flamyoad.tsukiviewer.ui.screens.home

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.Doujin
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * ViewModel for the Home screen.
 * Loads doujins from included directories and displays them in a grid.
 */
class HomeViewModel(
    private val application: Application,
    private val metadataRepo: MetadataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()
    
    private val selectedDoujins = mutableListOf<DoujinUiModel>()
    private var doujinListBuffer = mutableListOf<DoujinUiModel>()
    private var includedDirectories = listOf<File>()
    
    private var loadingJob: Job? = null
    
    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")
    
    init {
        loadDoujins()
    }
    
    fun loadDoujins() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            withContext(Dispatchers.IO) {
                // Load included directories from database
                includedDirectories = metadataRepo.pathDao.getAllBlocking()
                
                // Clear existing buffer
                doujinListBuffer.clear()
                
                // Scan each included directory
                for (dir in includedDirectories) {
                    walkDirectory(dir, dir)
                }
                
                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    _uiState.update { 
                        it.copy(
                            doujinList = doujinListBuffer.toList(),
                            isLoading = false
                        ) 
                    }
                }
            }
        }
    }
    
    /**
     * Recursively walks through directories to find doujin folders (folders containing images).
     */
    private suspend fun walkDirectory(currentDir: File, parentDir: File) {
        if (!currentDir.exists() || !currentDir.isDirectory) return
        
        val fileList = currentDir.listFiles() ?: return
        val imageList = fileList.filter { f -> f.extension.lowercase() in imageExtensions }
        
        // If this directory contains images, it's a doujin folder
        if (imageList.isNotEmpty()) {
            val sortedImages = imageList.sortedWith(
                compareBy<File> { it.name.length }.then(naturalOrder())
            )
            
            val coverImage = sortedImages.first().toUri()
            val title = currentDir.name
            val numberOfImages = imageList.size
            val lastModified = currentDir.lastModified()
            
            val doujinUiModel = DoujinUiModel(
                id = currentDir.absolutePath.hashCode().toLong(),
                coverUri = coverImage,
                title = title,
                numberOfItems = numberOfImages,
                lastModified = lastModified,
                path = currentDir.absolutePath,
                isSelected = false,
                shortTitle = null // Will be populated from metadata if available
            )
            
            // Check if already exists (avoid duplicates)
            val existingIndex = doujinListBuffer.indexOfFirst { it.path == doujinUiModel.path }
            if (existingIndex == -1) {
                doujinListBuffer.add(doujinUiModel)
            }
        }
        
        // Continue walking subdirectories
        for (file in fileList) {
            if (file.isDirectory) {
                walkDirectory(file, parentDir)
            }
        }
    }
    
    fun reloadDoujins() {
        loadDoujins()
    }
    
    fun setSortMode(mode: DoujinSortingMode) {
        _uiState.update { it.copy(sortMode = mode, isSorting = true) }
        
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                when (mode) {
                    DoujinSortingMode.TITLE_ASC -> doujinListBuffer.sortBy { it.title }
                    DoujinSortingMode.TITLE_DESC -> doujinListBuffer.sortByDescending { it.title }
                    DoujinSortingMode.DATE_ASC -> doujinListBuffer.sortBy { it.lastModified }
                    DoujinSortingMode.DATE_DESC -> doujinListBuffer.sortByDescending { it.lastModified }
                    DoujinSortingMode.NUM_ITEMS_ASC -> doujinListBuffer.sortBy { it.numberOfItems }
                    DoujinSortingMode.NUM_ITEMS_DESC -> doujinListBuffer.sortByDescending { it.numberOfItems }
                    DoujinSortingMode.PATH_ASC -> doujinListBuffer.sortBy { it.path }
                    DoujinSortingMode.PATH_DESC -> doujinListBuffer.sortByDescending { it.path }
                    DoujinSortingMode.SHORT_TITLE_ASC -> doujinListBuffer.sortBy { it.shortTitle ?: it.title }
                    DoujinSortingMode.SHORT_TITLE_DESC -> doujinListBuffer.sortByDescending { it.shortTitle ?: it.title }
                    DoujinSortingMode.NONE -> { /* No sorting */ }
                }
            }
            
            _uiState.update { 
                it.copy(
                    doujinList = doujinListBuffer.toList(),
                    isSorting = false
                ) 
            }
        }
    }
    
    fun setViewMode(mode: ViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }
    
    fun onDoujinClick(doujin: DoujinUiModel) {
        if (_uiState.value.isActionModeActive) {
            toggleDoujinSelection(doujin)
        } else {
            viewModelScope.launch {
                _events.emit(HomeEvent.NavigateToDoujinDetails(doujin.path))
            }
        }
    }
    
    fun onDoujinLongClick(doujin: DoujinUiModel) {
        if (!_uiState.value.isActionModeActive) {
            startActionMode()
        }
        toggleDoujinSelection(doujin)
    }
    
    fun startActionMode() {
        _uiState.update { it.copy(isActionModeActive = true) }
    }
    
    fun cancelActionMode() {
        selectedDoujins.clear()
        updateSelectionState()
        _uiState.update { 
            it.copy(
                isActionModeActive = false,
                selectedCount = 0
            ) 
        }
    }
    
    private fun toggleDoujinSelection(doujin: DoujinUiModel) {
        val isSelected = selectedDoujins.any { it.path == doujin.path }
        if (isSelected) {
            selectedDoujins.removeAll { it.path == doujin.path }
        } else {
            selectedDoujins.add(doujin)
        }
        
        if (selectedDoujins.isEmpty()) {
            cancelActionMode()
        } else {
            updateSelectionState()
            _uiState.update { it.copy(selectedCount = selectedDoujins.size) }
        }
    }
    
    private fun updateSelectionState() {
        val selectedPaths = selectedDoujins.map { it.path }.toSet()
        doujinListBuffer = doujinListBuffer.map { doujin ->
            doujin.copy(isSelected = doujin.path in selectedPaths)
        }.toMutableList()
        
        _uiState.update { it.copy(doujinList = doujinListBuffer.toList()) }
    }
    
    fun getSelectedDoujins(): List<DoujinUiModel> = selectedDoujins.toList()
    
    fun bookmarkSelectedDoujins() {
        val count = selectedDoujins.size
        // For now, show a snackbar - full implementation would use BookmarkRepository
        showSnackbar("Bookmarked $count item(s)")
        cancelActionMode()
    }
    
    fun editSelectedDoujins() {
        if (selectedDoujins.size == 1) {
            val path = selectedDoujins.first().path
            viewModelScope.launch {
                _events.emit(HomeEvent.NavigateToDoujinDetails(path))
            }
            cancelActionMode()
        } else {
            showSnackbar("Please select only one item to edit")
        }
    }
    
    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }
    
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

/**
 * Events that can be emitted by the HomeViewModel.
 */
sealed interface HomeEvent {
    data class NavigateToDoujinDetails(val doujinPath: String) : HomeEvent
    data class NavigateToSearch(val query: String = "") : HomeEvent
    data class ShowToast(val message: String) : HomeEvent
}

/**
 * Factory for creating HomeViewModel instances.
 */
class HomeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val db = AppDatabase.getInstance(application)
            val metadataRepo = MetadataRepository(
                context = application,
                db = db,
                pathDao = db.includedFolderDao(),
                doujinDetailsDao = db.doujinDetailsDao(),
                tagDao = db.tagsDao(),
                doujinTagDao = db.doujinTagDao(),
                folderDao = db.folderDao()
            )
            return HomeViewModel(application, metadataRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
