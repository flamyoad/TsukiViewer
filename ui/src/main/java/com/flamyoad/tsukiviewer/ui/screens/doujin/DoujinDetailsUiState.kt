package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.net.Uri

/**
 * UI state for the Doujin Details screen.
 */
data class DoujinDetailsUiState(
    val isLoading: Boolean = true,
    val directoryPath: String = "",
    val coverImage: Uri? = null,
    val imageList: List<Uri> = emptyList(),
    val fullTitleEnglish: String = "",
    val shortTitleEnglish: String = "",
    val fullTitleJapanese: String = "",
    val imageCount: Int = 0,
    val dateModified: String = "",
    val nukeCode: String? = null,
    val hasMetadata: Boolean = false,
    val tags: DoujinTagsUiModel = DoujinTagsUiModel(),
    val bookmarkGroups: List<BookmarkGroupUiModel> = emptyList(),
    val showBookmarkDialog: Boolean = false,
    val snackbarMessage: String? = null,
    val directoryNotFound: Boolean = false,
    val gridViewStyle: GridViewStyle = GridViewStyle.Grid
)

/**
 * UI model for tags organized by type.
 */
data class DoujinTagsUiModel(
    val parodies: List<TagUiModel> = emptyList(),
    val characters: List<TagUiModel> = emptyList(),
    val tags: List<TagUiModel> = emptyList(),
    val artists: List<TagUiModel> = emptyList(),
    val groups: List<TagUiModel> = emptyList(),
    val languages: List<TagUiModel> = emptyList(),
    val categories: List<TagUiModel> = emptyList()
) {
    fun isEmpty(): Boolean = parodies.isEmpty() && characters.isEmpty() && tags.isEmpty() &&
            artists.isEmpty() && groups.isEmpty() && languages.isEmpty() && categories.isEmpty()
}

/**
 * UI model for a single tag.
 */
data class TagUiModel(
    val id: Long,
    val name: String,
    val type: String,
    val url: String = "",
    val count: Int = 1
)

/**
 * UI model for a bookmark group with its selection state.
 */
data class BookmarkGroupUiModel(
    val id: Long,
    val name: String,
    val isTicked: Boolean = false
)

/**
 * View style for the image grid.
 */
enum class GridViewStyle {
    Grid,
    Scaled,
    Row,
    List
}
