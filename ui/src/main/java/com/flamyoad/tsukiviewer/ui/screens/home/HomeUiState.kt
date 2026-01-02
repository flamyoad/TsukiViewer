package com.flamyoad.tsukiviewer.ui.screens.home

import android.net.Uri

/**
 * Represents the UI state for the Home screen.
 */
data class HomeUiState(
    val doujinList: List<DoujinUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSorting: Boolean = false,
    val sortMode: DoujinSortingMode = DoujinSortingMode.NONE,
    val viewMode: ViewMode = ViewMode.SCALED,
    val selectedCount: Int = 0,
    val isActionModeActive: Boolean = false,
    val snackbarMessage: String? = null,
    val toastMessage: String? = null
)

/**
 * UI model for a Doujin item.
 */
data class DoujinUiModel(
    val id: Long,
    val coverUri: Uri,
    val title: String,
    val numberOfItems: Int,
    val lastModified: Long,
    val path: String,
    val isSelected: Boolean = false,
    val shortTitle: String? = null
)

/**
 * Enum for view modes (how doujins are displayed in the grid).
 */
enum class ViewMode {
    NORMAL_GRID,
    SCALED,
    MINI_GRID;
    
    fun getSpanCount(isPortrait: Boolean): Int {
        return when (this) {
            NORMAL_GRID -> if (isPortrait) 2 else 4
            MINI_GRID -> if (isPortrait) 3 else 5
            SCALED -> if (isPortrait) 2 else 4
        }
    }
}

/**
 * Enum for sorting modes.
 */
enum class DoujinSortingMode(val displayName: String) {
    TITLE_ASC("Title (A-Z)"),
    TITLE_DESC("Title (Z-A)"),
    DATE_ASC("Date (Oldest)"),
    DATE_DESC("Date (Newest)"),
    NUM_ITEMS_ASC("Pages (Low to High)"),
    NUM_ITEMS_DESC("Pages (High to Low)"),
    PATH_ASC("Path (A-Z)"),
    PATH_DESC("Path (Z-A)"),
    SHORT_TITLE_ASC("Short Title (A-Z)"),
    SHORT_TITLE_DESC("Short Title (Z-A)"),
    NONE("Default");
}
