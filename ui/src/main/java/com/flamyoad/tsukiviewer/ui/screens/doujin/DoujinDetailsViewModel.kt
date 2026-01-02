package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
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
import java.util.Arrays
import java.util.Date
import java.util.Locale

/**
 * ViewModel for the Doujin Details screen.
 */
class DoujinDetailsViewModel(
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

        viewModelScope.launch {
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

            // Load images and cover
            withContext(Dispatchers.IO) {
                val images = dir.listFiles { file ->
                    file.isFile && file.extension.lowercase() in imageExtensions
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

                // Sort images naturally
                val sortedImages = images.sortedWith(compareBy<File> { it.name.length }.then(naturalOrder()))
                val coverImage = sortedImages.first().toUri()
                val imageList = sortedImages.map { it.toUri() }
                val imageCount = sortedImages.size
                val lastModified = dateFormat.format(Date(dir.lastModified()))

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            coverImage = coverImage,
                            imageList = imageList,
                            imageCount = imageCount,
                            dateModified = lastModified,
                            fullTitleEnglish = dir.name
                        )
                    }
                }
            }

            // Load metadata from database
            loadMetadata(dir)
        }
    }

    private fun loadMetadata(dir: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val detailWithTags = metadataRepo.doujinDetailsDao.getLongDetailsByPathBlocking(dir.absolutePath)

                withContext(Dispatchers.Main) {
                    if (detailWithTags != null) {
                        updateFromMetadata(detailWithTags)
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                hasMetadata = false
                            )
                        }
                    }
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
                isLoading = false,
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

/**
 * Factory for creating DoujinDetailsViewModel instances.
 */
class DoujinDetailsViewModelFactory(
    private val application: Application,
    private val metadataRepo: MetadataRepository,
    private val bookmarkRepo: BookmarkRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoujinDetailsViewModel::class.java)) {
            return DoujinDetailsViewModel(application, metadataRepo, bookmarkRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
