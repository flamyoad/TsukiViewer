package com.flamyoad.tsukiviewer.ui.screens.collections

import android.net.Uri

/**
 * UI model for a collection.
 */
data class CollectionUiModel(
    val id: Long,
    val title: String,
    val coverImageUri: Uri?,
    val itemCount: Int = 0,
    val tags: List<String> = emptyList()
)

/**
 * UI state for the Collections screen.
 */
data class CollectionsUiState(
    val collections: List<CollectionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val viewStyle: CollectionViewStyle = CollectionViewStyle.GRID,
    val snackbarMessage: String? = null
)

/**
 * View style for collections display.
 */
enum class CollectionViewStyle {
    GRID,
    VERTICAL_LIST;
    
    fun getSpanCount(): Int = when (this) {
        GRID -> 2
        VERTICAL_LIST -> 1
    }
}
