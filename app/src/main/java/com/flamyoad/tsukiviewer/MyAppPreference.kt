package com.flamyoad.tsukiviewer

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import com.flamyoad.tsukiviewer.ui.doujinpage.GridViewStyle
import com.flamyoad.tsukiviewer.ui.reader.ReaderMode
import com.flamyoad.tsukiviewer.ui.reader.VolumeButtonScrollDirection
import com.flamyoad.tsukiviewer.ui.reader.VolumeButtonScrollMode
import com.flamyoad.tsukiviewer.ui.settings.preferences.FetchSourcePreference
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences
import com.flamyoad.tsukiviewer.ui.settings.preferences.VolumeButtonPreferences

// Note: Preference.xml won't be created until the Settings Activity is opened for first time
class MyAppPreference(context: Context) {
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        @Volatile
        private var INSTANCE: MyAppPreference? = null

        fun getInstance(context: Context): MyAppPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = MyAppPreference(context)

                INSTANCE = instance
                instance // return instance
            }
        }
    }

    fun getDefaultViewStyle(): GridViewStyle {
        val styleName = prefs.getString(
            MainPreferences.DEFAULT_GRID_VIEW_STYLE,
            GridViewStyle.Scaled.toString()
        )

        if (styleName != null) {
            return GridViewStyle.valueOf(styleName)
        } else {
            return GridViewStyle.Scaled
        }
    }

    fun setDefaultViewStyle(style: GridViewStyle) {
        put(MainPreferences.DEFAULT_GRID_VIEW_STYLE, style.toString())
    }

    fun getDefaultReaderMode(): ReaderMode {
        val name = prefs.getString(MainPreferences.DEFAULT_READER_MODE, ReaderMode.VerticalStrip.toString())
        if (name != null) {
            return ReaderMode.valueOf(name)
        } else {
            return ReaderMode.VerticalStrip
        }
    }

    fun shouldUseWindowsSort(): Boolean {
        return prefs.getBoolean(MainPreferences.USE_WINDOWS_EXPLORER_SORT, true)
    }

    fun askBeforeQuit(): Boolean {
        return prefs.getBoolean(MainPreferences.CONFIRM_BEFORE_QUIT, true)
    }

    fun shouldScrollWithVolumeButton(): Boolean {
        return prefs.getBoolean(MainPreferences.SCROLL_BY_VOLUME_BUTTONS, true)
    }

    fun getVolumeButtonScrollMode(): VolumeButtonScrollMode {
        val name = prefs.getString(VolumeButtonPreferences.SCROLL_MODE, "")
        return if (name != null && name.isNotBlank()) {
            VolumeButtonScrollMode.valueOf(name)
        } else {
            VolumeButtonScrollMode.PageByPage
        }
    }

    fun getVolumeUpAction(): VolumeButtonScrollDirection {
        val name = prefs.getString(VolumeButtonPreferences.VOLUME_UP_ACTION, "")
        return if (name != null && name.isNotBlank()) {
            VolumeButtonScrollDirection.valueOf(name)
        } else {
            VolumeButtonScrollDirection.GoToPrevPage
        }
    }

    fun getVolumeDownAction(): VolumeButtonScrollDirection {
        val name = prefs.getString(VolumeButtonPreferences.VOLUME_DOWN_ACTION, "")
        return if (name != null && name.isNotBlank()) {
            VolumeButtonScrollDirection.valueOf(name)
        } else {
            VolumeButtonScrollDirection.GoToNextPage
        }
    }

    fun getVolumeButtonScrollDistance(): Int {
        val value = prefs.getString(VolumeButtonPreferences.SCROLLING_DISTANCE, "0")
        return value?.toInt() ?: 0
    }

    fun shouldFetchFromHNexus(): Boolean {
        return prefs.getBoolean(FetchSourcePreference.FETCH_FROM_HENTAINEXUS, false)
    }

    fun getCollectionViewStyle(): Int {
        return prefs.getInt(MainPreferences.COLLECTION_VIEW_STYLE, CollectionListAdapter.LIST)
    }

    fun setCollectionViewStyle(style: Int) {
        put(MainPreferences.COLLECTION_VIEW_STYLE, style)
    }

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