package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.R

class ClearDataPreferences: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_clear_data, rootKey)
    }
}