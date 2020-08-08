package com.flamyoad.tsukiviewer.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.R

class FolderPreferences: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_main, rootKey)
    }
}