package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.net.Uri

/**
 * Reader mode options for the doujin reader.
 */
enum class ReaderMode(val displayName: String) {
    HORIZONTAL_SWIPE("Horizontal Swipe"),
    VERTICAL_SWIPE("Vertical Swipe"),
    VERTICAL_STRIP("Vertical Strip (Webtoon)")
}

/**
 * Volume button scroll direction options.
 */
enum class VolumeButtonScrollDirection {
    GO_TO_NEXT_PAGE,
    GO_TO_PREV_PAGE,
    NOTHING
}

/**
 * UI state for the doujin reader screen.
 */
data class ReaderUiState(
    val isLoading: Boolean = true,
    val directoryPath: String = "",
    val directoryName: String = "",
    val imageList: List<Uri> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val readerMode: ReaderMode = ReaderMode.HORIZONTAL_SWIPE,
    val showControls: Boolean = true,
    val showModeSelector: Boolean = false,
    val showThumbnailBar: Boolean = false,
    val directoryNotFound: Boolean = false,
    val snackbarMessage: String? = null,
    // Volume button settings
    val scrollWithVolumeButtons: Boolean = false,
    val volumeUpAction: VolumeButtonScrollDirection = VolumeButtonScrollDirection.NOTHING,
    val volumeDownAction: VolumeButtonScrollDirection = VolumeButtonScrollDirection.NOTHING
)
