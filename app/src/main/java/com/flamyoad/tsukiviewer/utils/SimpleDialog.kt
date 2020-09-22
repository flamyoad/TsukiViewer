package com.flamyoad.tsukiviewer.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SimpleDialog: DialogFragment() {

    private var positiveAction: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle("Testing")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                positiveAction.invoke() // This never gets called
            })
        }

        return builder.create()
    }

    companion object {
        fun newInstance(positiveAction: () -> Unit): SimpleDialog {
            val dialog = SimpleDialog().apply {
                this.positiveAction = positiveAction
            }
            return dialog
        }
    }
}