package com.flamyoad.tsukiviewer.ui.settings.preferences

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.SettingsViewModel

class ClearDataPreferences: PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by activityViewModels()

    private var btnClearMetadata: Preference? = null
    private var btnClearTags: Preference? = null
    private var btnClearSearchHistory: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_clear_data, rootKey)

        btnClearMetadata = findPreference("pref_clear_metadata")
        btnClearTags = findPreference("pref_clear_tags")
        btnClearSearchHistory = findPreference("pref_clear_search_history")

        btnClearMetadata?.setOnPreferenceClickListener {
            showDialog("Clear all metadata?",
                viewModel::clearMetadata)
            return@setOnPreferenceClickListener true
        }

        btnClearTags?.setOnPreferenceClickListener {
            showDialog("Clear all tags?",
                viewModel::clearTags)
            return@setOnPreferenceClickListener true
        }

        btnClearSearchHistory?.setOnPreferenceClickListener {
            showDialog("Clear search history?",
                viewModel::clearSearchHistory)
            return@setOnPreferenceClickListener true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.isRemovingItems().observe(viewLifecycleOwner, Observer { isRemoving ->
            when (isRemoving) {
                true -> {
                    btnClearMetadata?.isEnabled = false
                    btnClearTags?.isEnabled = false
                    btnClearSearchHistory?.isEnabled = false
                }
                false -> {
                    btnClearMetadata?.isEnabled = true
                    btnClearTags?.isEnabled = true
                    btnClearSearchHistory?.isEnabled = true
                }
            }
        })
    }

    private fun showDialog(title: String, deleteAction: () -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage("This action cannot be undone")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                deleteAction.invoke()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i -> })

        builder.show()
    }

}