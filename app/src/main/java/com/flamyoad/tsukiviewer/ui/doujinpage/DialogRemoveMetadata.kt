package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

class DialogRemoveMetadata : DialogFragment() {
    private val viewModel: DoujinViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle("Remove title & tags")
            setMessage("This action cannot be undone. Continue?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                viewModel.removeMetadata()
            })
            setNegativeButton("Return") { dialog, which->

            }
        }

        return builder.create()
    }

    companion object {
        fun newInstance(): DialogRemoveMetadata {
            val dialog = DialogRemoveMetadata().apply {
                arguments = Bundle().apply {

                }
            }
            return dialog
        }
    }
}