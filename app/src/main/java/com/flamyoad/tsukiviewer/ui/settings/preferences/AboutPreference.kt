package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.R

class AboutPreference: PreferenceFragmentCompat() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_about_me, rootKey)

        val steamProfile: Preference? = findPreference("pref_steam_profile")
        steamProfile?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://steamcommunity.com/id/flamyoad/"))
            startActivity(intent)

            return@setOnPreferenceClickListener true
        }

        val versionNumberPref: Preference? = findPreference("pref_version_number")
        versionNumberPref?.summary = getVersionNumber()
    }

    private fun getVersionNumber(): String {
        try {
            val manager = context?.packageManager
            val info = manager?.getPackageInfo(context?.packageName.orEmpty(), 0)

            val versionName = info?.versionName
            return versionName ?: ""

        } catch (e: Exception) {
            return ""
        }
    }
}