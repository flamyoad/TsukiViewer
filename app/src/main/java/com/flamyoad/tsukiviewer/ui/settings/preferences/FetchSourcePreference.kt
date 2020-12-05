package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.R

class FetchSourcePreference : PreferenceFragmentCompat() {
    private lateinit var appPreference: MyAppPreference
    private lateinit var sharedPrefs: SharedPreferences

    private var hentaiNexusSwitch: SwitchPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_fetch_sources, rootKey)

        appPreference = MyAppPreference.getInstance(requireContext())
        sharedPrefs = appPreference.prefs

        hentaiNexusSwitch = findPreference(FETCH_FROM_HENTAINEXUS)

        hentaiNexusSwitch?.isChecked = sharedPrefs.getBoolean(FETCH_FROM_HENTAINEXUS, false)

        hentaiNexusSwitch?.setOnPreferenceChangeListener { preference, newValue ->
            appPreference.put(FETCH_FROM_HENTAINEXUS, newValue as Boolean)
            return@setOnPreferenceChangeListener true
        }
    }

    companion object {
        const val FETCH_FROM_NHENTAI = "pref_fetch_from_nhentai"
        const val FETCH_FROM_HENTAINEXUS = "pref_fetch_from_hentainexus"
    }
}