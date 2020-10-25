package com.flamyoad.tsukiviewer

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.flamyoad.tsukiviewer.ui.doujinpage.GridViewStyle
import com.flamyoad.tsukiviewer.ui.reader.ReaderMode
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences

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

    fun put(key: String, value: String) {
        prefs.edit()
            .putString(key, value)
            .apply()
    }

    fun put(key: String, value: Int) {
        prefs.edit()
            .putInt(key, value)
            .apply()
    }

    fun put(key: String, value: Boolean) {
        prefs.edit()
            .putBoolean(key, value)
            .apply()
    }

    fun put(key: String, value: Float) {
        prefs.edit()
            .putFloat(key, value)
            .apply()
    }

    fun put(key: String, value: Long) {
        prefs.edit()
            .putLong(key, value)
            .apply()
    }
}