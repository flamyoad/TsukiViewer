package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.R

class DialogSortDoujin : DialogFragment() {

    private val viewModel: LocalDoujinViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_sort_doujin, null, false)

        builder.apply {
            setView(view)
        }

        val dialog = builder.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // Removes the unused white title bar

        val buttonIdList = DoujinSortingMode.getAllLayoutId()

        for (buttonId in buttonIdList) {
            val button = view.findViewById<ImageView>(buttonId)
            button.setOnClickListener {
                val mode = DoujinSortingMode.fromLayout(buttonId)
                viewModel.setSortMode(mode)
                dialog.dismiss()
            }
        }

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.sortMode().observe(this, Observer {
            if (it == DoujinSortingMode.NONE) {
                return@Observer
            }

            val button = dialog?.findViewById<ImageView>(it.getLayoutId())
            val gray = ContextCompat.getColor(requireContext(), R.color.subtleGray)

            button?.setBackgroundColor(gray)
//            button?.setBackgroundResource(R.drawable.doujin_sort_dialog_item_border)
        })
    }

    companion object {
        fun newInstance() = DialogSortDoujin().apply {
            arguments = Bundle().apply {

            }
        }
    }
}