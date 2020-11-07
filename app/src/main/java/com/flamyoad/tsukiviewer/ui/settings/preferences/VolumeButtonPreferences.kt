package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.reader.VolumeButtonScrollMode
import com.flamyoad.tsukiviewer.ui.settings.SettingsViewModel

class VolumeButtonPreferences : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var appPreference: MyAppPreference

    private var scrollMode: ListPreference? = null
    private var volumeScrollDistance: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_scroll_volume_buton, rootKey)

        appPreference = MyAppPreference.getInstance(requireContext())

        scrollMode = findPreference(SCROLL_MODE)
        volumeScrollDistance = findPreference(SCROLLING_DISTANCE)

        volumeScrollDistance?.isEnabled =
            appPreference.getVolumeButtonScrollMode() == VolumeButtonScrollMode.FixedDistance

        scrollMode?.setOnPreferenceChangeListener { preference, newValue ->
            val scrollMode = when (newValue == null) {
                true -> VolumeButtonScrollMode.PageByPage
                false -> VolumeButtonScrollMode.valueOf(newValue.toString())
            }

            volumeScrollDistance?.isEnabled = scrollMode == VolumeButtonScrollMode.FixedDistance
            appPreference.put(SCROLL_MODE, scrollMode.toString())

            return@setOnPreferenceChangeListener true
        }
    }

    companion object {
        const val VOLUME_UP_ACTION = "pref_volume_up_action"
        const val VOLUME_DOWN_ACTION = "pref_volume_down_action"
        const val SCROLL_MODE = "pref_volume_scroll_mode"
        const val SCROLLING_DISTANCE = "pref_volume_scroll_distance"
    }
}