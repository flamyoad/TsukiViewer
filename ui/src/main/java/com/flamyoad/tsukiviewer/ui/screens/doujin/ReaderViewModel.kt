package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Arrays
import java.util.Locale

/**
 * ViewModel for the Doujin Reader screen.
 */
class ReaderViewModel(
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")
    private var currentPath: String = ""

    /**
     * Load images from the given directory path.
     */
    fun loadImages(dirPath: String, initialPage: Int = 0) {
        if (dirPath == currentPath && !_uiState.value.isLoading) {
            return
        }
        currentPath = dirPath

        _uiState.update { 
            it.copy(
                isLoading = true, 
                directoryPath = dirPath,
                directoryName = File(dirPath).name
            ) 
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dir = File(dirPath)

                // Check if directory exists
                if (!dir.exists() || !dir.isDirectory) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                directoryNotFound = true,
                                snackbarMessage = "Directory does not exist"
                            )
                        }
                    }
                    return@withContext
                }

                // Load images
                val images = dir.listFiles { file ->
                    file.isFile && file.extension.lowercase(Locale.ROOT) in imageExtensions
                }

                if (images == null || images.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                directoryNotFound = true,
                                snackbarMessage = "No images found in directory"
                            )
                        }
                    }
                    return@withContext
                }

                // Natural sort (Windows Explorer style)
                Arrays.sort(images) { o1, o2 ->
                    naturalCompare(o1.name, o2.name)
                }

                val imageUris = images.map { it.toUri() }
                val validInitialPage = initialPage.coerceIn(0, images.size - 1)

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            imageList = imageUris,
                            totalPages = imageUris.size,
                            currentPage = validInitialPage,
                            directoryNotFound = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Update the current page number.
     */
    fun setCurrentPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    /**
     * Toggle controls visibility.
     */
    fun toggleControls() {
        _uiState.update { it.copy(showControls = !it.showControls) }
    }

    /**
     * Show/hide controls explicitly.
     */
    fun setControlsVisible(visible: Boolean) {
        _uiState.update { it.copy(showControls = visible) }
    }

    /**
     * Toggle mode selector visibility.
     */
    fun toggleModeSelector() {
        _uiState.update { 
            it.copy(
                showModeSelector = !it.showModeSelector,
                showThumbnailBar = if (!it.showModeSelector) false else it.showThumbnailBar
            ) 
        }
    }

    /**
     * Toggle thumbnail bar visibility.
     */
    fun toggleThumbnailBar() {
        _uiState.update { 
            it.copy(
                showThumbnailBar = !it.showThumbnailBar,
                showModeSelector = if (!it.showThumbnailBar) false else it.showModeSelector
            ) 
        }
    }

    /**
     * Hide overlays (mode selector and thumbnail bar).
     */
    fun hideOverlays() {
        _uiState.update { 
            it.copy(showModeSelector = false, showThumbnailBar = false) 
        }
    }

    /**
     * Set the reader mode.
     */
    fun setReaderMode(mode: ReaderMode) {
        _uiState.update { 
            it.copy(readerMode = mode, showModeSelector = false) 
        }
    }

    /**
     * Clear snackbar message.
     */
    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    /**
     * Natural string comparison (Windows Explorer style sorting).
     */
    private fun naturalCompare(s1: String, s2: String): Int {
        val pattern = Regex("(\\d+|\\D+)")
        val parts1 = pattern.findAll(s1).map { it.value }.toList()
        val parts2 = pattern.findAll(s2).map { it.value }.toList()

        for (i in 0 until minOf(parts1.size, parts2.size)) {
            val p1 = parts1[i]
            val p2 = parts2[i]

            val result = if (p1[0].isDigit() && p2[0].isDigit()) {
                val n1 = p1.toBigIntegerOrNull() ?: return p1.compareTo(p2)
                val n2 = p2.toBigIntegerOrNull() ?: return p1.compareTo(p2)
                n1.compareTo(n2)
            } else {
                p1.lowercase(Locale.ROOT).compareTo(p2.lowercase(Locale.ROOT))
            }

            if (result != 0) return result
        }

        return parts1.size - parts2.size
    }

    /**
     * Factory for creating ReaderViewModel.
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReaderViewModel::class.java)) {
                return ReaderViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
