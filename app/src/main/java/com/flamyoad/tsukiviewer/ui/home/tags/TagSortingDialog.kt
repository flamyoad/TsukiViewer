package com.flamyoad.tsukiviewer.ui.home.tags

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.flamyoad.tsukiviewer.model.TagSortingMode

private const val CHOSEN_MODE_STRING = "CHOSEN_MODE_STRING"

class TagSortingDialog: DialogFragment() {
    private val viewModel: DoujinTagsViewModel by activityViewModels()

    private var chosenMode = TagSortingMode.NAME_ASCENDING

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CHOSEN_MODE_STRING, chosenMode.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val choiceList = TagSortingMode.values()
            .map { mode -> mode.getDescription() }
            .toTypedArray() // char[]

        // Ticks the sorting mode applied for the list
        chosenMode = viewModel.getCurrentMode()

        // If screen is rotated while dialog is still active, restore the choice chosen by the user.
        // At this point, the sorting mode chosen in the dialog has not been applied to the list yet.
        if (savedInstanceState != null) {
            val name = savedInstanceState.getString(CHOSEN_MODE_STRING)
            if (name != null) {
                chosenMode = TagSortingMode.valueOf(name)
            }
        }

        val choice = choiceList.indexOf(chosenMode.getDescription())

        builder.apply {
            setTitle("Sort by")
            setSingleChoiceItems(choiceList, choice) { dialog, which ->
                val mode = TagSortingMode.fromDescription(choiceList[which])
                chosenMode = mode
            }
            setPositiveButton("Ok") { dialog, which ->
                viewModel.setSortingMode(chosenMode)
            }
            setNegativeButton("Return") { dialog , which ->

            }
        }
        return builder.create()
    }
}