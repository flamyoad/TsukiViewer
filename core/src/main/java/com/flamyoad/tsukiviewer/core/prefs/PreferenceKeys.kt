package com.flamyoad.tsukiviewer.core.prefs

/**
 * Centralized preference keys used across the app.
 * This allows both XML Settings and Compose Settings to use the same keys.
 */
object PreferenceKeys {
    // Main preferences
    const val USE_EXTERNAL_GALLERY = "pref_external_gallery_switch"
    const val EXTERNAL_IMAGE_VIEWER = "pref_external_img_viewer"
    const val EXTERNAL_GALLERY_PKG_NAME = "external_gallery_package_name"
    const val DEFAULT_GRID_VIEW_STYLE = "pref_grid_view_style"
    const val CONFIRM_BEFORE_QUIT = "pref_confirm_before_quit"
    const val DEFAULT_READER_MODE = "pref_reader_mode"
    const val USE_WINDOWS_EXPLORER_SORT = "pref_use_windows_explorer_sort"
    const val SCROLL_BY_VOLUME_BUTTONS = "pref_scroll_by_volume_button"
    const val COLLECTION_VIEW_STYLE = "pref_collection_view_style"
    const val DOUJIN_VIEW_MODE = "pref_doujin_view_mode"
    const val DOUJIN_DETAILS_LANDING_SCR = "pref_doujin_details_landing_screen"
    const val USE_COMPOSE_UI = "pref_use_compose_ui"
    
    // Volume button preferences
    const val VOLUME_UP_ACTION = "pref_volume_up_action"
    const val VOLUME_DOWN_ACTION = "pref_volume_down_action"
    const val SCROLL_MODE = "pref_volume_scroll_mode"
    const val SCROLLING_DISTANCE = "pref_volume_scroll_distance"
    
    // Fetch source preferences
    const val FETCH_FROM_NHENTAI = "pref_fetch_from_nhentai"
    const val FETCH_FROM_HENTAINEXUS = "pref_fetch_from_hentainexus"
}
