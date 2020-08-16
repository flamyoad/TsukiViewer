package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.GalleryPickerDialog

class FolderPreferences : PreferenceFragmentCompat(), GalleryPickListener {

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

        externalGallerySwitch = findPreference("pref_external_gallery_switch")
        externalGalleryPicker = findPreference("pref_external_img_viewer")

        externalGallerySwitch?.isChecked = prefs.getBoolean(USE_EXTERNAL_GALLERY, false)

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
        val dialog = GalleryPickerDialog(this)
        dialog.show(fragmentTransaction, "dialog_gallery_picker")
    }

    override fun onGalleryPick(packageName: String) {
        prefs.edit()
            .putString(EXTERNAL_GALLERY_PKG_NAME, packageName)
            .apply()

        externalGalleryPicker?.summary = packageName

        val dialog = requireActivity()
            .supportFragmentManager
            .findFragmentByTag("dialog_gallery_picker") as DialogFragment

        dialog.dismiss()
    }
}

interface GalleryPickListener {
    fun onGalleryPick(packageName: String)
}