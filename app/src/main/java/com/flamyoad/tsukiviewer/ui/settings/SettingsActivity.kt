package com.flamyoad.tsukiviewer.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.preferences.FolderPreferences
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FolderPreferences())
            .commit()
    }
}
