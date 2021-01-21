package com.flamyoad.tsukiviewer.ui.settings

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences
import com.flamyoad.tsukiviewer.utils.extensions.toast
import kotlinx.android.synthetic.main.activity_settings.*

private const val TOOLBAR_TITLE = "settings_toolbar_title"

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainPreferences())
                .commit()
        } else {
            setTitle(savedInstanceState.getString(TOOLBAR_TITLE))
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                supportActionBar?.title = "Settings"
            }
        }

        viewModel.isRemovingItems().observe(this, Observer { isRemoving ->
            when (isRemoving) {
                true -> {
                    indicatorRemoveItems.visibility = View.VISIBLE
                    container.alpha = 0.6f
                }

                false -> {
                    indicatorRemoveItems.visibility = View.GONE
                    container.alpha = 1f
                    toast("Done")
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putCharSequence(TOOLBAR_TITLE, getTitle())
    }

    override fun onNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onNavigateUp()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
        if (pref == null)
            return false

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

        supportActionBar?.title = pref.title

        return true
    }
}
