package com.flamyoad.tsukiviewer.core.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Core preference manager that provides basic SharedPreferences operations.
 * This is placed in the core module so it can be used by both XML and Compose UI modules.
 */
class MyAppPreference(context: Context) {
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        @Volatile
        private var INSTANCE: MyAppPreference? = null

        fun getInstance(context: Context): MyAppPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = MyAppPreference(context)
                INSTANCE = instance
                instance
            }
        }
    }

    // Boolean preferences
    fun shouldUseWindowsSort(): Boolean {
        return prefs.getBoolean(PreferenceKeys.USE_WINDOWS_EXPLORER_SORT, true)
    }

    fun askBeforeQuit(): Boolean {
        return prefs.getBoolean(PreferenceKeys.CONFIRM_BEFORE_QUIT, true)
    }

    fun shouldScrollWithVolumeButton(): Boolean {
        return prefs.getBoolean(PreferenceKeys.SCROLL_BY_VOLUME_BUTTONS, true)
    }

    fun shouldFetchFromHNexus(): Boolean {
        return prefs.getBoolean(PreferenceKeys.FETCH_FROM_HENTAINEXUS, false)
    }
    
    fun useComposeUi(): Boolean {
        return prefs.getBoolean(PreferenceKeys.USE_COMPOSE_UI, true)
    }

    // String preferences with defaults
    fun getDefaultGridViewStyle(): String {
        return prefs.getString(PreferenceKeys.DEFAULT_GRID_VIEW_STYLE, "Scaled") ?: "Scaled"
    }

    fun setDefaultGridViewStyle(style: String) {
        put(PreferenceKeys.DEFAULT_GRID_VIEW_STYLE, style)
    }

    fun getDefaultReaderMode(): String {
        return prefs.getString(PreferenceKeys.DEFAULT_READER_MODE, "VerticalStrip") ?: "VerticalStrip"
    }

    fun setDefaultReaderMode(mode: String) {
        put(PreferenceKeys.DEFAULT_READER_MODE, mode)
    }

    fun getDefaultLandingPage(): String {
        return prefs.getString(PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR, "DoujinDetails") ?: "DoujinDetails"
    }

    fun getVolumeButtonScrollMode(): String {
        return prefs.getString(PreferenceKeys.SCROLL_MODE, "PageByPage") ?: "PageByPage"
    }

    fun getVolumeUpAction(): String {
        return prefs.getString(PreferenceKeys.VOLUME_UP_ACTION, "GoToPrevPage") ?: "GoToPrevPage"
    }

    fun getVolumeDownAction(): String {
        return prefs.getString(PreferenceKeys.VOLUME_DOWN_ACTION, "GoToNextPage") ?: "GoToNextPage"
    }

    fun getVolumeButtonScrollDistance(): Int {
        val value = prefs.getString(PreferenceKeys.SCROLLING_DISTANCE, "0")
        return value?.toIntOrNull() ?: 0
    }

    // Collection view style (stored as Int)
    fun getCollectionViewStyle(): Int {
        return prefs.getInt(PreferenceKeys.COLLECTION_VIEW_STYLE, 0) // 0 = LIST
    }

    fun setCollectionViewStyle(style: Int) {
        put(PreferenceKeys.COLLECTION_VIEW_STYLE, style)
    }

    // Doujin view mode
    fun getDoujinViewMode(): String {
        return prefs.getString(PreferenceKeys.DOUJIN_VIEW_MODE, "SCALED") ?: "SCALED"
    }

    fun setDoujinViewMode(viewMode: String) {
        put(PreferenceKeys.DOUJIN_VIEW_MODE, viewMode)
    }

    // Generic put methods
    fun put(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun put(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun put(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}
