package com.flamyoad.tsukiviewer.ui.home.bookmarks

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import javax.inject.Inject

class DialogDeleteItems(): DialogFragment() {
    
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: BookmarkViewModel by activityViewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = parentFragment as DeleteItemsListener

        val builder = AlertDialog.Builder(requireContext())

        val count = viewModel.selectedBookmarkCount()
        val title = if (count > 1)
            "Remove $count bookmarks?"
        else
            "Remove $count bookmark?"

        builder.apply {
            setTitle(title)
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                listener.deleteItems()
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            setItems(
                viewModel.selectedBookmarkNames(),
                DialogInterface.OnClickListener { dialogInterface, i -> })
        }

        val dialog = builder.create()

        dialog.listView.setOnItemClickListener { adapterView, view, i, l ->
            // Does nothing. Replaces the default listener just to prevent the
            // dialog from closing itself when clicking on one of the items
        }

        return builder.create()
    }

    companion object {
        fun newInstance(): DialogDeleteItems {
            return DialogDeleteItems()
        }
    }
}

interface DeleteItemsListener {
    fun deleteItems()
}