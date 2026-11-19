package com.flamyoad.tsukiviewer.ui.screens.settings

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.ReaderSpreadPreferences
import com.flamyoad.tsukiviewer.core.repository.RelatedMangaRepository
import com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository
import kotlinx.coroutines.CancellationException
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.ui.screens.doujin.GridViewStyle
import com.flamyoad.tsukiviewer.ui.screens.doujin.ReaderMode

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
            val scrollByVolumeButtons = prefs.getBoolean(PREF_SCROLL_BY_VOLUME, true)
            val confirmBeforeQuit = prefs.getBoolean(PREF_CONFIRM_QUIT, true)
            val readerMode = ReaderMode.fromPreference(prefs.getString(PREF_READER_MODE, null))
            val gridStyle = GridViewStyle.fromPreference(prefs.getString(PREF_GRID_STYLE, null))
            val windowsSort = prefs.getBoolean(PREF_WINDOWS_SORT, true)
            val viewMode = prefs.getString(PREF_VIEW_MODE, null)
            val spreads = ReaderSpreadPreferences.read(prefs)
            val useComposeUi = prefs.getBoolean(PREF_USE_COMPOSE_UI, true)
            
            _uiState.update { 
                it.copy(
                    useExternalGallery = useExternalGallery,
                    externalGalleryPackage = externalGalleryPackage,
                    scrollByVolumeButtons = scrollByVolumeButtons,
                    confirmBeforeQuit = confirmBeforeQuit,
                    readerRightToLeft = spreads.rightToLeft,
                    automaticSpreads = spreads.enabled,
                    separateCover = spreads.separateCover,
                    defaultReaderMode = readerMode,
                    defaultGridViewStyle = gridStyle,
                    openImageGridFirst = prefs.getString(PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR, null) == "ImageGrid",
                    readerGifAutoplay = prefs.getBoolean(PreferenceKeys.READER_GIF_AUTOPLAY, true),
                    useWindowsExplorerSort = windowsSort,
                    doujinViewMode = DoujinViewMode.entries.firstOrNull { it.name == viewMode }
                        ?: viewMode?.toIntOrNull()?.let { DoujinViewMode.entries.getOrNull(it) }
                        ?: DoujinViewMode.SCALED,
                    useComposeUi = useComposeUi,
                    showRelatedManga = prefs.getBoolean(
                        PreferenceKeys.SHOW_RELATED_MANGA, PreferenceKeys.SHOW_RELATED_MANGA_DEFAULT
                    ),
                    isLoading = false
                )
            }
        }
    }
    
    fun setReaderRightToLeft(value: Boolean) {
        prefs.edit { putBoolean(PreferenceKeys.READER_RIGHT_TO_LEFT, value) }
        _uiState.update { it.copy(readerRightToLeft = value) }
    }

    fun setShowRelatedManga(value: Boolean) {
        prefs.edit { putBoolean(PreferenceKeys.SHOW_RELATED_MANGA, value) }
        _uiState.update { it.copy(showRelatedManga = value) }
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
    
    fun setAutomaticSpreads(value: Boolean) {
        ReaderSpreadPreferences.read(prefs).copy(enabled = value).save(prefs)
        _uiState.update { it.copy(automaticSpreads = value) }
    }

    fun setSeparateCover(value: Boolean) {
        ReaderSpreadPreferences.read(prefs).copy(separateCover = value).save(prefs)
        _uiState.update { it.copy(separateCover = value) }
    }

    fun clearData(option: ClearDataOption) {
        if (_uiState.value.clearingData) return
        _uiState.update { it.copy(clearingData = true) }
        viewModelScope.launch {
            try {
                val db = AppDatabase.getInstance(application)
                when (option) {
                    ClearDataOption.METADATA -> {
                        db.withTransaction {
                            db.doujinTagDao().deleteAll()
                            db.doujinDetailsDao().deleteAll()
                        }
                        RelatedMangaRepository.invalidateLibrary(db)
                    }
                    ClearDataOption.TAGS -> {
                        db.withTransaction {
                            db.doujinTagDao().deleteAll()
                            db.tagsDao().deleteAll()
                        }
                        RelatedMangaRepository.invalidateLibrary(db)
                    }
                    ClearDataOption.SEARCH_HISTORY -> SearchHistoryRepository(db.searchHistoryDao()).deleteAll()
                }
                _uiState.update { it.copy(snackbarMessage = "Data cleared") }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Could not clear data") }
            } finally {
                _uiState.update { it.copy(clearingData = false) }
            }
        }
    }

    fun getPreference(key: String, default: String): String = prefs.getString(key, default) ?: default

    fun setPreference(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun setLandingScreen(openImageGrid: Boolean) {
        prefs.edit {
            putString(PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR, if (openImageGrid) "ImageGrid" else "DoujinDetails")
        }
        _uiState.update { it.copy(openImageGridFirst = openImageGrid) }
    }

    fun setReaderGifAutoplay(value: Boolean) {
        prefs.edit { putBoolean(PreferenceKeys.READER_GIF_AUTOPLAY, value) }
        _uiState.update { it.copy(readerGifAutoplay = value) }
    }

    fun setDefaultReaderMode(mode: ReaderMode) {
        // Store as String for compatibility with XML ListPreference
        prefs.edit { putString(PREF_READER_MODE, mode.preferenceValue) }
        _uiState.update { it.copy(defaultReaderMode = mode) }
    }
    
    fun setDefaultGridViewStyle(style: GridViewStyle) {
        // Store as String for compatibility with XML ListPreference
        prefs.edit { putString(PREF_GRID_STYLE, style.name) }
        _uiState.update { it.copy(defaultGridViewStyle = style) }
    }
    
    fun setUseWindowsExplorerSort(value: Boolean) {
        prefs.edit { putBoolean(PREF_WINDOWS_SORT, value) }
        _uiState.update { it.copy(useWindowsExplorerSort = value) }
    }
    
    fun setDoujinViewMode(mode: DoujinViewMode) {
        // Store as String for compatibility with XML ListPreference
        prefs.edit { putString(PREF_VIEW_MODE, mode.name) }
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
        }.distinctBy(GalleryAppInfo::packageName).sortedBy(GalleryAppInfo::appName)
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

enum class ClearDataOption(val title: String) {
    METADATA("Clear doujin metadata"),
    TAGS("Clear tags"),
    SEARCH_HISTORY("Clear search history")
}
