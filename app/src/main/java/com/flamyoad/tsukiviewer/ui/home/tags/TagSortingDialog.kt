package com.flamyoad.tsukiviewer.ui.home.tags

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.flamyoad.tsukiviewer.model.TagSortingMode

class TagSortingDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val sortingChoices = TagSortingMode.values()
            .map { mode -> mode.getDescription() }
            .toTypedArray() // char[]

        builder.apply {
            setTitle("Sort by")
            setSingleChoiceItems(sortingChoices, -1) { dialog, which ->

            }
            setPositiveButton("Ok") { dialog, which ->

            }
            setNegativeButton("Return") { dialog , which ->

            }
        }
        return builder.create()
    }
}