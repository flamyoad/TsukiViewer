package com.flamyoad.tsukiviewer.utils.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SimpleDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle("title")
            setMessage("text")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

            })
            setNegativeButton("Return") { dialog, which->

            }
        }

        return builder.create()
    }

    companion object {
        fun newInstance(): SimpleDialog {
            val dialog = SimpleDialog().apply {
                arguments = Bundle().apply {

                }
            }
            return dialog
        }
    }
}