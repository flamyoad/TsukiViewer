package com.flamyoad.tsukiviewer

import android.content.Context
import android.content.SharedPreferences
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import com.flamyoad.tsukiviewer.core.model.ViewMode
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference as CoreMyAppPreference
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.ui.doujinpage.GridViewStyle
import com.flamyoad.tsukiviewer.ui.doujinpage.LandingPageMode
import com.flamyoad.tsukiviewer.ui.reader.ReaderMode
import com.flamyoad.tsukiviewer.ui.reader.VolumeButtonScrollDirection
import com.flamyoad.tsukiviewer.ui.reader.VolumeButtonScrollMode

/**
 * App-specific preference manager that wraps the core MyAppPreference.
 * This class provides type-safe access to preferences using app-specific enums.
 * 
 * For basic preference access, use [CoreMyAppPreference] from the core module.
 */
class MyAppPreference private constructor(context: Context) {
    private val corePreference = CoreMyAppPreference.getInstance(context)
    val prefs: SharedPreferences get() = corePreference.prefs

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

    fun getDefaultViewStyle(): GridViewStyle {
        val styleName = corePreference.getDefaultGridViewStyle()
        return try {
            GridViewStyle.valueOf(styleName)
        } catch (e: IllegalArgumentException) {
            GridViewStyle.Scaled
        }
    }

    fun setDefaultViewStyle(style: GridViewStyle) {
        corePreference.setDefaultGridViewStyle(style.toString())
    }

    fun getDefaultReaderMode(): ReaderMode {
        val name = corePreference.getDefaultReaderMode()
        return try {
            ReaderMode.valueOf(name)
        } catch (e: IllegalArgumentException) {
            ReaderMode.VerticalStrip
        }
    }

    fun setDefaultReaderMode(mode: ReaderMode) {
        corePreference.setDefaultReaderMode(mode.toString())
    }

    fun getDefaultLandingPage(): LandingPageMode {
        val name = corePreference.getDefaultLandingPage()
        return if (name.isNotBlank()) {
            try {
                LandingPageMode.valueOf(name)
            } catch (e: IllegalArgumentException) {
                LandingPageMode.DoujinDetails
            }
        } else {
            LandingPageMode.DoujinDetails
        }
    }

    fun shouldUseWindowsSort(): Boolean = corePreference.shouldUseWindowsSort()

    fun askBeforeQuit(): Boolean = corePreference.askBeforeQuit()

    fun shouldScrollWithVolumeButton(): Boolean = corePreference.shouldScrollWithVolumeButton()

    fun getVolumeButtonScrollMode(): VolumeButtonScrollMode {
        val name = corePreference.getVolumeButtonScrollMode()
        return if (name.isNotBlank()) {
            try {
                VolumeButtonScrollMode.valueOf(name)
            } catch (e: IllegalArgumentException) {
                VolumeButtonScrollMode.PageByPage
            }
        } else {
            VolumeButtonScrollMode.PageByPage
        }
    }

    fun getVolumeUpAction(): VolumeButtonScrollDirection {
        val name = corePreference.getVolumeUpAction()
        return if (name.isNotBlank()) {
            try {
                VolumeButtonScrollDirection.valueOf(name)
            } catch (e: IllegalArgumentException) {
                VolumeButtonScrollDirection.GoToPrevPage
            }
        } else {
            VolumeButtonScrollDirection.GoToPrevPage
        }
    }

    fun getVolumeDownAction(): VolumeButtonScrollDirection {
        val name = corePreference.getVolumeDownAction()
        return if (name.isNotBlank()) {
            try {
                VolumeButtonScrollDirection.valueOf(name)
            } catch (e: IllegalArgumentException) {
                VolumeButtonScrollDirection.GoToNextPage
            }
        } else {
            VolumeButtonScrollDirection.GoToNextPage
        }
    }

    fun getVolumeButtonScrollDistance(): Int = corePreference.getVolumeButtonScrollDistance()

    fun shouldFetchFromHNexus(): Boolean = corePreference.shouldFetchFromHNexus()

    fun getCollectionViewStyle(): Int {
        val style = corePreference.getCollectionViewStyle()
        return if (style == 0) CollectionListAdapter.LIST else style
    }

    fun setCollectionViewStyle(style: Int) {
        corePreference.setCollectionViewStyle(style)
    }

    fun getDoujinViewMode(): ViewMode {
        val name = corePreference.getDoujinViewMode()
        return try {
            ViewMode.valueOf(name)
        } catch (e: IllegalArgumentException) {
            ViewMode.SCALED
        }
    }

    fun setDoujinViewMode(viewMode: ViewMode) {
        corePreference.setDoujinViewMode(viewMode.toString())
    }

    fun put(key: String, value: String) = corePreference.put(key, value)

    fun put(key: String, value: Int) = corePreference.put(key, value)

    fun put(key: String, value: Boolean) = corePreference.put(key, value)
}