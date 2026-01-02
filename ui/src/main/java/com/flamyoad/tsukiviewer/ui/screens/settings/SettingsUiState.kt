package com.flamyoad.tsukiviewer.ui.screens.settings

/**
 * UI state for settings screen.
 */
data class SettingsUiState(
    val useExternalGallery: Boolean = false,
    val externalGalleryPackage: String = "",
    val scrollByVolumeButtons: Boolean = false,
    val confirmBeforeQuit: Boolean = true,
    val defaultReaderMode: ReaderMode = ReaderMode.HORIZONTAL_SWIPE,
    val defaultGridViewStyle: GridViewStyle = GridViewStyle.SCALED,
    val useWindowsExplorerSort: Boolean = false,
    val doujinViewMode: DoujinViewMode = DoujinViewMode.NORMAL_GRID,
    val useComposeUi: Boolean = true,  // Toggle between Compose and XML views
    val isLoading: Boolean = false,
    val showGalleryPickerDialog: Boolean = false,
    val snackbarMessage: String? = null
)

/**
 * Information about a gallery app.
 */
data class GalleryAppInfo(
    val packageName: String,
    val appName: String
)

/**
 * Reader mode options.
 */
enum class ReaderMode(val displayName: String) {
    HORIZONTAL_SWIPE("Horizontal Swipe"),
    VERTICAL_SWIPE("Vertical Swipe"),
    VERTICAL_STRIP("Vertical Strip (Webtoon)")
}

/**
 * Grid view style options.
 */
enum class GridViewStyle(val displayName: String) {
    SCALED("Scaled"),
    GRID("Grid"),
    LIST("List")
}

/**
 * Doujin view mode options.
 */
enum class DoujinViewMode(val displayName: String) {
    NORMAL_GRID("Normal Grid"),
    MINI_GRID("Mini Grid"),
    SCALED("Scaled")
}
