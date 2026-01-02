package com.flamyoad.tsukiviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences

/**
 * Launcher Activity that routes to either Compose or XML UI based on user preference.
 * 
 * This activity checks the "pref_use_compose_ui" preference and launches either
 * [ComposeMainActivity] or [MainActivity] accordingly.
 */
class LauncherActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo: should use di here but w/e
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val useComposeUi = sharedPrefs.getBoolean(MainPreferences.USE_COMPOSE_UI, true)
        
        val targetActivity = if (useComposeUi) {
            ComposeMainActivity::class.java
        } else {
            MainActivity::class.java
        }
        
        val intent = Intent(this, targetActivity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
