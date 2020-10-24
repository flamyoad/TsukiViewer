package com.flamyoad.tsukiviewer.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainPreferences())
            .commit()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
        if (pref == null) return false

        // Instantiate the new Fragment
        val args = pref.extras

        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment)

        fragment.apply {
            arguments = args
            setTargetFragment(caller, 0)
        }

        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()

        return true
    }
}
