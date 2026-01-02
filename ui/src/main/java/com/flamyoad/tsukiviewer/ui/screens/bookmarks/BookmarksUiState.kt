package com.flamyoad.tsukiviewer.ui.screens.bookmarks

import android.net.Uri

/**
 * UI model for a bookmark group.
 */
data class BookmarkGroupUiModel(
    val name: String,
    val thumbnailUri: Uri? = null,
    val itemCount: Int = 0,
    val lastModified: Long = 0,
    val isSelected: Boolean = false
)

/**
 * UI model for a bookmarked doujin item.
 */
data class BookmarkItemUiModel(
    val id: Long,
    val title: String,
    val absolutePath: String,
    val coverImageUri: Uri? = null,
    val isSelected: Boolean = false
)

/**
 * UI state for the Bookmarks screen.
 */
data class BookmarksUiState(
    val groups: List<BookmarkGroupUiModel> = emptyList(),
    val items: List<BookmarkItemUiModel> = emptyList(),
    val selectedGroupName: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isSearchActive: Boolean = false,
    val isActionModeActive: Boolean = false,
    val selectedCount: Int = 0,
    val showCreateGroupDialog: Boolean = false,
    val snackbarMessage: String? = null
) {
    val currentGroup: BookmarkGroupUiModel?
        get() = groups.find { it.name == selectedGroupName }
}
