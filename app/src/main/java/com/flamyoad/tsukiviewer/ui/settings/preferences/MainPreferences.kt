package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.GalleryAppPickerDialog
import com.flamyoad.tsukiviewer.ui.settings.SettingsViewModel

class MainPreferences : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val USE_EXTERNAL_GALLERY = "use_external_gallery"
        const val EXTERNAL_GALLERY_PKG_NAME = "external_gallery_package_name"
    }

    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var appPreference: MyAppPreference
    private lateinit var sharedPrefs: SharedPreferences

    private var externalGallerySwitch: SwitchPreference? = null
    private var externalGalleryPicker: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_main, rootKey)

        appPreference = MyAppPreference.getInstance(requireContext())
        sharedPrefs = appPreference.prefs

        sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        externalGallerySwitch = findPreference("pref_external_gallery_switch")
        externalGallerySwitch?.isChecked = sharedPrefs.getBoolean(USE_EXTERNAL_GALLERY, false)

        externalGalleryPicker = findPreference("pref_external_img_viewer")
        externalGalleryPicker?.isEnabled = sharedPrefs.getBoolean(USE_EXTERNAL_GALLERY, false)

        externalGallerySwitch?.setOnPreferenceChangeListener { preference, newValue ->
            val useExternalGallery = newValue as Boolean
            externalGalleryPicker?.isEnabled = useExternalGallery
            appPreference.put(USE_EXTERNAL_GALLERY, useExternalGallery)

            return@setOnPreferenceChangeListener true
        }

        externalGalleryPicker?.summary = sharedPrefs.getString(EXTERNAL_GALLERY_PKG_NAME, "")

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

