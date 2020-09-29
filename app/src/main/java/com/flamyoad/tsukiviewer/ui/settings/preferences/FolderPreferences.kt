package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.GalleryAppPickerDialog
import com.flamyoad.tsukiviewer.ui.settings.SettingsViewModel

class FolderPreferences : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var prefs: SharedPreferences

    private var externalGallerySwitch: SwitchPreference? = null
    private var externalGalleryPicker: Preference? = null

    companion object {
        const val USE_EXTERNAL_GALLERY = "use_external_gallery"
        const val EXTERNAL_GALLERY_PKG_NAME = "external_gallery_package_name"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_main, rootKey)

        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        prefs.registerOnSharedPreferenceChangeListener(this)

        externalGallerySwitch = findPreference("pref_external_gallery_switch")
        externalGallerySwitch?.isChecked = prefs.getBoolean(USE_EXTERNAL_GALLERY, false)

        externalGalleryPicker = findPreference("pref_external_img_viewer")
        externalGalleryPicker?.isEnabled = prefs.getBoolean(USE_EXTERNAL_GALLERY, false)

        externalGallerySwitch?.setOnPreferenceChangeListener { preference, newValue ->
            val useExternalGallery = newValue as Boolean

            externalGalleryPicker?.isEnabled = useExternalGallery

            prefs.edit()
                .putBoolean(USE_EXTERNAL_GALLERY, useExternalGallery)
                .apply()

            return@setOnPreferenceChangeListener true
        }

        externalGalleryPicker?.summary = prefs.getString(EXTERNAL_GALLERY_PKG_NAME, "")

        externalGalleryPicker?.setOnPreferenceClickListener {
            showGalleryPicker()
            return@setOnPreferenceClickListener true
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
            EXTERNAL_GALLERY_PKG_NAME -> {
                externalGalleryPicker?.summary = prefs?.getString(key, "")
            }
        }
    }
}

