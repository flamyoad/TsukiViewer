package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Doujin Details screen.
 */
class DoujinDetailsViewModel @Inject constructor(
    private val application: Application,
    private val metadataRepo: MetadataRepository,
    private val bookmarkRepo: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoujinDetailsUiState())
    val uiState: StateFlow<DoujinDetailsUiState> = _uiState.asStateFlow()

    private var currentPath: String = ""
    private var bookmarkGroupTickStatus = hashMapOf<String, Boolean>()

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    fun loadDoujinDetails(dirPath: String) {
        if (dirPath == currentPath && !_uiState.value.isLoading) {
            return
        }
        currentPath = dirPath

        _uiState.update { it.copy(isLoading = true, directoryPath = dirPath) }

        viewModelScope.launch(Dispatchers.IO) {
            val dir = File(dirPath)

            // Check if directory exists
            if (!dir.exists() || !dir.isDirectory) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        directoryNotFound = true,
                        snackbarMessage = "Directory does not exist"
                    ) 
                }
                return@launch
            }

            // Load images - this is the heavy operation
            val images = dir.listFiles { file ->
                file.isFile && file.extension.lowercase() in imageExtensions
            }

            if (images == null || images.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        directoryNotFound = true,
                        snackbarMessage = "No images found in directory"
                    )
                }
                return@launch
            }

            // Sort images naturally (do this on IO thread)
            val sortedImages = images.sortedWith(compareBy<File> { it.name.length }.then(naturalOrder()))
            val coverImage = sortedImages.first().toUri()
            val imageCount = sortedImages.size
            val lastModified = dateFormat.format(Date(dir.lastModified()))

            // Update UI with cover first (fast path for initial render)
            _uiState.update {
                it.copy(
                    coverImage = coverImage,
                    imageCount = imageCount,
                    dateModified = lastModified,
                    fullTitleEnglish = dir.name,
                    isLoading = false
                )
            }

            // Load metadata in parallel
            loadMetadataAsync(dir)

            // Load full image list after UI is shown (lazy load for Images tab)
            val imageList = sortedImages.map { it.toUri() }
            _uiState.update { it.copy(imageList = imageList) }
        }
    }

    private fun loadMetadataAsync(dir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val detailWithTags = metadataRepo.doujinDetailsDao.getLongDetailsByPathBlocking(dir.absolutePath)

            if (detailWithTags != null) {
                updateFromMetadata(detailWithTags)
            } else {
                _uiState.update {
                    it.copy(hasMetadata = false)
                }
            }
        }
    }

    private fun updateFromMetadata(details: DoujinDetailsWithTags) {
        val tagGroups = details.tags.groupBy { it.type }

        val tagsUiModel = DoujinTagsUiModel(
            parodies = tagGroups["parody"]?.map { it.toUiModel() } ?: emptyList(),
            characters = tagGroups["character"]?.map { it.toUiModel() } ?: emptyList(),
            tags = tagGroups["tag"]?.map { it.toUiModel() } ?: emptyList(),
            artists = tagGroups["artist"]?.map { it.toUiModel() } ?: emptyList(),
            groups = tagGroups["group"]?.map { it.toUiModel() } ?: emptyList(),
            languages = tagGroups["language"]?.map { it.toUiModel() } ?: emptyList(),
            categories = tagGroups["category"]?.map { it.toUiModel() } ?: emptyList()
        )

        val nukeCode = details.doujinDetails.nukeCode.let { 
            if (it == -1) null else it.toString() 
        }

        _uiState.update {
            it.copy(
                hasMetadata = true,
                fullTitleEnglish = details.doujinDetails.fullTitleEnglish,
                shortTitleEnglish = details.doujinDetails.shortTitleEnglish,
                fullTitleJapanese = details.doujinDetails.fullTitleJapanese,
                nukeCode = nukeCode,
                tags = tagsUiModel
            )
        }
    }

    private fun com.flamyoad.tsukiviewer.core.model.Tag.toUiModel(): TagUiModel {
        return TagUiModel(
            id = this.tagId ?: 0L,
            name = this.name,
            type = this.type,
            url = this.url,
            count = this.count
        )
    }

    fun loadBookmarkGroups() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val currentDir = File(currentPath)
                val groups = bookmarkRepo.getAllGroupsFrom(currentDir)
                
                bookmarkGroupTickStatus.clear()
                groups.filter { it.isTicked }.forEach { 
                    bookmarkGroupTickStatus[it.name] = true 
                }

                val groupUiModels = groups.map { group ->
                    BookmarkGroupUiModel(
                        id = group.name.hashCode().toLong(),
                        name = group.name,
                        isTicked = group.isTicked
                    )
                }

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            bookmarkGroups = groupUiModels,
                            showBookmarkDialog = true
                        )
                    }
                }
            }
        }
    }

    fun toggleBookmarkGroup(groupName: String) {
        val currentStatus = bookmarkGroupTickStatus[groupName] ?: false
        bookmarkGroupTickStatus[groupName] = !currentStatus

        _uiState.update { state ->
            state.copy(
                bookmarkGroups = state.bookmarkGroups.map { group ->
                    if (group.name == groupName) {
                        group.copy(isTicked = !currentStatus)
                    } else {
                        group
                    }
                }
            )
        }
    }

    fun saveBookmarkSelections() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val status = bookmarkRepo.wipeAndInsertNew(
                    File(currentPath), 
                    bookmarkGroupTickStatus
                )

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            showBookmarkDialog = false,
                            snackbarMessage = status
                        )
                    }
                }
            }
        }
    }

    fun createNewBookmarkGroup(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bookmarkRepo.insertGroup(BookmarkGroup(name))
                
                // Reload groups to include the new one
                val currentDir = File(currentPath)
                val groups = bookmarkRepo.getAllGroupsFrom(currentDir)

                bookmarkGroupTickStatus.clear()
                groups.filter { it.isTicked }.forEach {
                    bookmarkGroupTickStatus[it.name] = true
                }

                val groupUiModels = groups.map { group ->
                    BookmarkGroupUiModel(
                        id = group.name.hashCode().toLong(),
                        name = group.name,
                        isTicked = group.isTicked
                    )
                }

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(bookmarkGroups = groupUiModels)
                    }
                }
            }
        }
    }

    fun dismissBookmarkDialog() {
        _uiState.update { it.copy(showBookmarkDialog = false) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun setGridViewStyle(style: GridViewStyle) {
        _uiState.update { it.copy(gridViewStyle = style) }
    }

    fun getNukeCode(): String? = _uiState.value.nukeCode
}
