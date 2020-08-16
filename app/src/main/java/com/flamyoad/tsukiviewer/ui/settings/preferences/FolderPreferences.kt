package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.GalleryPickerDialog

class FolderPreferences : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_main, rootKey)

        val externalGallerySwitch: SwitchPreference? = findPreference("pref_external_gallery_switch")
        val externalGalleryPicker: Preference? = findPreference("pref_external_img_viewer")

        externalGallerySwitch?.setOnPreferenceChangeListener { preference, newValue ->
            externalGalleryPicker?.isEnabled = newValue as Boolean
            return@setOnPreferenceChangeListener true
        }

        externalGalleryPicker?.setOnPreferenceClickListener {
            val fm = requireActivity().supportFragmentManager
            val fragmentTransaction = fm.beginTransaction()
            val dialog = GalleryPickerDialog()
            dialog.show(fragmentTransaction, "dialog_gallery_picker")

            return@setOnPreferenceClickListener true
        }
    }

}