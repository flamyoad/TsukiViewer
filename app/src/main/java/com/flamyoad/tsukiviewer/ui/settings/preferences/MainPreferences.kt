package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.flamyoad.tsukiviewer.LauncherActivity
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.ui.settings.GalleryAppPickerDialog
import com.flamyoad.tsukiviewer.ui.settings.SettingsViewModel
import androidx.core.content.edit

class MainPreferences : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var appPreference: MyAppPreference
    private lateinit var sharedPrefs: SharedPreferences

    private var externalGallerySwitch: SwitchPreference? = null
    private var externalGalleryPicker: Preference? = null
    private var volumeButtonScrollSwitch: SwitchPreference? = null
    private var useComposeUiSwitch: SwitchPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_main, rootKey)

        appPreference = MyAppPreference.getInstance(requireContext())
        sharedPrefs = appPreference.prefs

        sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        externalGallerySwitch = findPreference(PreferenceKeys.USE_EXTERNAL_GALLERY)
        externalGalleryPicker = findPreference(PreferenceKeys.EXTERNAL_IMAGE_VIEWER)
        volumeButtonScrollSwitch = findPreference(PreferenceKeys.SCROLL_BY_VOLUME_BUTTONS)

        externalGallerySwitch?.isChecked = sharedPrefs.getBoolean(PreferenceKeys.USE_EXTERNAL_GALLERY, false)

        externalGalleryPicker?.isEnabled = sharedPrefs.getBoolean(PreferenceKeys.USE_EXTERNAL_GALLERY, false)

        externalGallerySwitch?.setOnPreferenceChangeListener { preference, newValue ->
            val useExternalGallery = newValue as Boolean
            externalGalleryPicker?.isEnabled = useExternalGallery
            appPreference.put(PreferenceKeys.USE_EXTERNAL_GALLERY, useExternalGallery)
            return@setOnPreferenceChangeListener true
        }

        externalGalleryPicker?.summary = sharedPrefs.getString(PreferenceKeys.EXTERNAL_GALLERY_PKG_NAME, "")

        externalGalleryPicker?.setOnPreferenceClickListener {
            showGalleryPicker()
            return@setOnPreferenceClickListener true
        }

        volumeButtonScrollSwitch?.setOnPreferenceChangeListener { preference, newValue ->
            appPreference.put(PreferenceKeys.SCROLL_BY_VOLUME_BUTTONS, newValue as Boolean)
            return@setOnPreferenceChangeListener true
        }

        useComposeUiSwitch = findPreference(PreferenceKeys.USE_COMPOSE_UI)
        useComposeUiSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val useComposeUi = newValue as Boolean
            sharedPrefs.edit(commit = true) { putBoolean(PreferenceKeys.USE_COMPOSE_UI, useComposeUi) }
            Handler(Looper.getMainLooper()).postDelayed({
                restartApp()
            }, 1000)
            return@setOnPreferenceChangeListener false
        }

    }

    private fun showGalleryPicker() {
        val fm = requireActivity().supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        val dialog = GalleryAppPickerDialog.newInstance()
        dialog.show(fragmentTransaction, "dialog_gallery_picker")
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        when (key) {
            PreferenceKeys.EXTERNAL_GALLERY_PKG_NAME -> {
                externalGalleryPicker?.summary = prefs?.getString(key, "")
            }
        }
    }
    
    private fun restartApp() {
        val context = requireContext()
        val intent = Intent(context, LauncherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        // Deprecated: Use PreferenceKeys from core module instead
        @Deprecated("Use PreferenceKeys.USE_EXTERNAL_GALLERY", ReplaceWith("PreferenceKeys.USE_EXTERNAL_GALLERY", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val USE_EXTERNAL_GALLERY = PreferenceKeys.USE_EXTERNAL_GALLERY
        @Deprecated("Use PreferenceKeys.EXTERNAL_IMAGE_VIEWER", ReplaceWith("PreferenceKeys.EXTERNAL_IMAGE_VIEWER", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val EXTERNAL_IMAGE_VIEWER = PreferenceKeys.EXTERNAL_IMAGE_VIEWER
        @Deprecated("Use PreferenceKeys.EXTERNAL_GALLERY_PKG_NAME", ReplaceWith("PreferenceKeys.EXTERNAL_GALLERY_PKG_NAME", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val EXTERNAL_GALLERY_PKG_NAME = PreferenceKeys.EXTERNAL_GALLERY_PKG_NAME
        @Deprecated("Use PreferenceKeys.DEFAULT_GRID_VIEW_STYLE", ReplaceWith("PreferenceKeys.DEFAULT_GRID_VIEW_STYLE", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val DEFAULT_GRID_VIEW_STYLE = PreferenceKeys.DEFAULT_GRID_VIEW_STYLE
        @Deprecated("Use PreferenceKeys.CONFIRM_BEFORE_QUIT", ReplaceWith("PreferenceKeys.CONFIRM_BEFORE_QUIT", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val CONFIRM_BEFORE_QUIT = PreferenceKeys.CONFIRM_BEFORE_QUIT
        @Deprecated("Use PreferenceKeys.DEFAULT_READER_MODE", ReplaceWith("PreferenceKeys.DEFAULT_READER_MODE", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val DEFAULT_READER_MODE = PreferenceKeys.DEFAULT_READER_MODE
        @Deprecated("Use PreferenceKeys.USE_WINDOWS_EXPLORER_SORT", ReplaceWith("PreferenceKeys.USE_WINDOWS_EXPLORER_SORT", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val USE_WINDOWS_EXPLORER_SORT = PreferenceKeys.USE_WINDOWS_EXPLORER_SORT
        @Deprecated("Use PreferenceKeys.SCROLL_BY_VOLUME_BUTTONS", ReplaceWith("PreferenceKeys.SCROLL_BY_VOLUME_BUTTONS", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val SCROLL_BY_VOLUME_BUTTONS = PreferenceKeys.SCROLL_BY_VOLUME_BUTTONS
        @Deprecated("Use PreferenceKeys.COLLECTION_VIEW_STYLE", ReplaceWith("PreferenceKeys.COLLECTION_VIEW_STYLE", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val COLLECTION_VIEW_STYLE = PreferenceKeys.COLLECTION_VIEW_STYLE
        @Deprecated("Use PreferenceKeys.DOUJIN_VIEW_MODE", ReplaceWith("PreferenceKeys.DOUJIN_VIEW_MODE", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val DOUJIN_VIEW_MODE = PreferenceKeys.DOUJIN_VIEW_MODE
        @Deprecated("Use PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR", ReplaceWith("PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val DOUJIN_DETAILS_LANDING_SCR = PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR
        @Deprecated("Use PreferenceKeys.USE_COMPOSE_UI", ReplaceWith("PreferenceKeys.USE_COMPOSE_UI", "com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys"))
        const val USE_COMPOSE_UI = PreferenceKeys.USE_COMPOSE_UI
    }
}

