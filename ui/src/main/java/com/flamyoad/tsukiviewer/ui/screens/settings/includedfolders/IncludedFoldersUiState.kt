package com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders

import java.io.File

/**
 * UI model representing an included folder path.
 */
data class IncludedFolderUiModel(
    val id: Long,
    val path: String,
    val displayName: String,
    val exists: Boolean = true
)

/**
 * UI state for the Included Folders screen.
 */
data class IncludedFoldersUiState(
    val folders: List<IncludedFolderUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val showFolderPicker: Boolean = false,
    val snackbarMessage: String? = null,
    val hasStoragePermission: Boolean = false,
    val needsManageStoragePermission: Boolean = false
)
