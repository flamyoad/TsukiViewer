package com.flamyoad.tsukiviewer.ui.screens.settings

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.content.edit

/**
 * ViewModel for the Settings screen.
 */
class SettingsViewModel(
    private val application: Application
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    init {
        loadSettings()
    }
    
    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Load from shared preferences
            val useExternalGallery = prefs.getBoolean(PREF_USE_EXTERNAL_GALLERY, false)
            val externalGalleryPackage = prefs.getString(PREF_EXTERNAL_GALLERY_PKG, "") ?: ""
            val scrollByVolumeButtons = prefs.getBoolean(PREF_SCROLL_BY_VOLUME, false)
            val confirmBeforeQuit = prefs.getBoolean(PREF_CONFIRM_QUIT, true)
            // XML ListPreference stores values as Strings, so we need to parse them
            val readerModeOrdinal = prefs.getString(PREF_READER_MODE, "0")?.toIntOrNull() ?: 0
            val gridStyleOrdinal = prefs.getString(PREF_GRID_STYLE, "0")?.toIntOrNull() ?: 0
            val windowsSort = prefs.getBoolean(PREF_WINDOWS_SORT, false)
            val viewModeOrdinal = prefs.getString(PREF_VIEW_MODE, "0")?.toIntOrNull() ?: 0
            val useComposeUi = prefs.getBoolean(PREF_USE_COMPOSE_UI, true)
            
            _uiState.update { 
                it.copy(
                    useExternalGallery = useExternalGallery,
                    externalGalleryPackage = externalGalleryPackage,
                    scrollByVolumeButtons = scrollByVolumeButtons,
                    confirmBeforeQuit = confirmBeforeQuit,
                    defaultReaderMode = ReaderMode.entries.getOrElse(readerModeOrdinal) { ReaderMode.HORIZONTAL_SWIPE },
                    defaultGridViewStyle = GridViewStyle.entries.getOrElse(gridStyleOrdinal) { GridViewStyle.SCALED },
                    useWindowsExplorerSort = windowsSort,
                    doujinViewMode = DoujinViewMode.entries.getOrElse(viewModeOrdinal) { DoujinViewMode.NORMAL_GRID },
                    useComposeUi = useComposeUi,
                    isLoading = false
                )
            }
        }
    }
    
    fun setUseExternalGallery(value: Boolean) {
        prefs.edit { putBoolean(PREF_USE_EXTERNAL_GALLERY, value) }
        _uiState.update { it.copy(useExternalGallery = value) }
    }
    
    fun setScrollByVolumeButtons(value: Boolean) {
        prefs.edit { putBoolean(PREF_SCROLL_BY_VOLUME, value) }
        _uiState.update { it.copy(scrollByVolumeButtons = value) }
    }
    
    fun setConfirmBeforeQuit(value: Boolean) {
        prefs.edit { putBoolean(PREF_CONFIRM_QUIT, value) }
        _uiState.update { it.copy(confirmBeforeQuit = value) }
    }
    
    fun setDefaultReaderMode(mode: ReaderMode) {
        // Store as String for compatibility with XML ListPreference
        prefs.edit { putString(PREF_READER_MODE, mode.ordinal.toString()) }
        _uiState.update { it.copy(defaultReaderMode = mode) }
    }
    
    fun setDefaultGridViewStyle(style: GridViewStyle) {
        // Store as String for compatibility with XML ListPreference
        prefs.edit { putString(PREF_GRID_STYLE, style.ordinal.toString()) }
        _uiState.update { it.copy(defaultGridViewStyle = style) }
    }
    
    fun setUseWindowsExplorerSort(value: Boolean) {
        prefs.edit { putBoolean(PREF_WINDOWS_SORT, value) }
        _uiState.update { it.copy(useWindowsExplorerSort = value) }
    }
    
    fun setDoujinViewMode(mode: DoujinViewMode) {
        // Store as String for compatibility with XML ListPreference
        prefs.edit { putString(PREF_VIEW_MODE, mode.ordinal.toString()) }
        _uiState.update { it.copy(doujinViewMode = mode) }
    }
    
    fun setUseComposeUi(value: Boolean) {
        prefs.edit(commit = true) { putBoolean(PREF_USE_COMPOSE_UI, value) }
        _uiState.update { it.copy(useComposeUi = value) }
    }
    
    fun setExternalGalleryPackage(packageName: String) {
        prefs.edit { putString(PREF_EXTERNAL_GALLERY_PKG, packageName) }
        _uiState.update { it.copy(externalGalleryPackage = packageName) }
    }
    
    fun showGalleryPicker() {
        _uiState.update { it.copy(showGalleryPickerDialog = true) }
    }
    
    fun hideGalleryPicker() {
        _uiState.update { it.copy(showGalleryPickerDialog = false) }
    }
    
    fun getImageViewerApps(): List<GalleryAppInfo> {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            type = "image/*"
        }
        val pkgAppList = application.packageManager.queryIntentActivities(intent, 0)
        return pkgAppList.map { resolveInfo ->
            GalleryAppInfo(
                packageName = resolveInfo.activityInfo.packageName,
                appName = resolveInfo.loadLabel(application.packageManager).toString()
            )
        }
    }
    
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
    
    companion object {
        private const val PREF_USE_EXTERNAL_GALLERY = "pref_external_gallery_switch"
        private const val PREF_EXTERNAL_GALLERY_PKG = "external_gallery_package_name"
        private const val PREF_SCROLL_BY_VOLUME = "pref_scroll_by_volume_button"
        private const val PREF_CONFIRM_QUIT = "pref_confirm_before_quit"
        private const val PREF_READER_MODE = "pref_reader_mode"
        private const val PREF_GRID_STYLE = "pref_grid_view_style"
        private const val PREF_WINDOWS_SORT = "pref_use_windows_explorer_sort"
        private const val PREF_VIEW_MODE = "pref_doujin_view_mode"
        private const val PREF_USE_COMPOSE_UI = "pref_use_compose_ui"
    }
}

/**
 * Factory for creating SettingsViewModel instances.
 */
class SettingsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
