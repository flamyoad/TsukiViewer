package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import javax.inject.Inject

class DialogRemoveMetadata : DialogFragment() {
    
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: DoujinViewModel by activityViewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

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