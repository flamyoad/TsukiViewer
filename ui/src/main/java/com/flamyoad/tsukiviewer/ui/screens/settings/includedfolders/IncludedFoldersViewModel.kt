package com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders

import android.app.Application
import android.os.Build
import android.os.Environment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.IncludedPath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for the Included Folders screen.
 */
class IncludedFoldersViewModel(
    private val application: Application
) : ViewModel() {
    
    private val database = AppDatabase.getInstance(application)
    private val pathDao = database.includedFolderDao()
    
    private val _uiState = MutableStateFlow(IncludedFoldersUiState())
    val uiState: StateFlow<IncludedFoldersUiState> = _uiState.asStateFlow()
    
    // Observer for LiveData from DAO
    private val pathListObserver = Observer<List<IncludedPath>> { paths ->
        val folderModels = paths.map { path ->
            val file = path.dir
            IncludedFolderUiModel(
                id = file.absolutePath.hashCode().toLong(), // Use path hash as ID
                path = file.absolutePath,
                displayName = file.name,
                exists = file.exists()
            )
        }
        _uiState.update { it.copy(
            folders = folderModels,
            isLoading = false
        ) }
    }
    
    init {
        checkStoragePermission()
        loadFolders()
    }
    
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val hasPermission = Environment.isExternalStorageManager()
            _uiState.update { it.copy(
                hasStoragePermission = hasPermission,
                needsManageStoragePermission = !hasPermission
            ) }
        } else {
            // For older versions, permission is handled by the Activity
            _uiState.update { it.copy(hasStoragePermission = true) }
        }
    }
    
    private fun loadFolders() {
        _uiState.update { it.copy(isLoading = true) }
        // Observe LiveData - note: this requires observeForever since we're not in a lifecycle-aware component
        pathDao.getAll().observeForever(pathListObserver)
    }
    
    override fun onCleared() {
        super.onCleared()
        // Remove observer when ViewModel is cleared
        pathDao.getAll().removeObserver(pathListObserver)
    }
    
    fun addFolder(path: String) {
        viewModelScope.launch {
            try {
                val file = File(path)
                if (!file.exists()) {
                    _uiState.update { it.copy(snackbarMessage = "Folder does not exist") }
                    return@launch
                }
                if (!file.isDirectory) {
                    _uiState.update { it.copy(snackbarMessage = "Selected path is not a folder") }
                    return@launch
                }
                
                val includedPath = IncludedPath(dir = file.canonicalFile)
                pathDao.insert(includedPath)
                _uiState.update { it.copy(snackbarMessage = "Folder added: ${file.name}") }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Failed to add folder: ${e.message}") }
            }
        }
    }
    
    fun removeFolder(folderPath: String) {
        viewModelScope.launch {
            try {
                val folders = _uiState.value.folders
                val folderToRemove = folders.find { it.path == folderPath }
                if (folderToRemove != null) {
                    val includedPath = IncludedPath(dir = File(folderToRemove.path))
                    pathDao.delete(includedPath)
                    _uiState.update { it.copy(snackbarMessage = "Folder removed: ${folderToRemove.displayName}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Failed to remove folder: ${e.message}") }
            }
        }
    }
    
    fun showFolderPicker() {
        _uiState.update { it.copy(showFolderPicker = true) }
    }
    
    fun hideFolderPicker() {
        _uiState.update { it.copy(showFolderPicker = false) }
    }
    
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
    
    fun onStoragePermissionGranted() {
        _uiState.update { it.copy(
            hasStoragePermission = true,
            needsManageStoragePermission = false
        ) }
    }
}

/**
 * Factory for creating IncludedFoldersViewModel.
 */
class IncludedFoldersViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncludedFoldersViewModel::class.java)) {
            return IncludedFoldersViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
