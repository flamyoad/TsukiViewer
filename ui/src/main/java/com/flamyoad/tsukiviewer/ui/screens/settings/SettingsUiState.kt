package com.flamyoad.tsukiviewer.ui.screens.settings

import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.ui.screens.doujin.GridViewStyle
import com.flamyoad.tsukiviewer.ui.screens.doujin.ReaderMode

/**
 * UI state for settings screen.
 */
data class SettingsUiState(
    val useExternalGallery: Boolean = false,
    val externalGalleryPackage: String = "",
    val scrollByVolumeButtons: Boolean = false,
    val confirmBeforeQuit: Boolean = true,
    val readerRightToLeft: Boolean = false,
    val automaticSpreads: Boolean = true,
    val separateCover: Boolean = true,
    val clearingData: Boolean = false,
    val defaultReaderMode: ReaderMode = ReaderMode.VERTICAL_STRIP,
    val defaultGridViewStyle: GridViewStyle = GridViewStyle.Scaled,
    val openImageGridFirst: Boolean = false,
    val readerGifAutoplay: Boolean = true,
    val useWindowsExplorerSort: Boolean = false,
    val doujinViewMode: DoujinViewMode = DoujinViewMode.NORMAL_GRID,
    val showRelatedManga: Boolean = PreferenceKeys.SHOW_RELATED_MANGA_DEFAULT,
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
 * Doujin view mode options.
 */
enum class DoujinViewMode(val displayName: String) {
    NORMAL_GRID("Normal Grid"),
    MINI_GRID("Mini Grid"),
    SCALED("Scaled")
}
