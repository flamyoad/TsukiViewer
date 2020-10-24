package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.R

class AboutPreference: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_about_me, rootKey)

        val steamProfile: Preference? = findPreference("pref_steam_profile")
        steamProfile?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://steamcommunity.com/id/flamyoad/"))
            startActivity(intent)

            return@setOnPreferenceClickListener true
        }
    }
}