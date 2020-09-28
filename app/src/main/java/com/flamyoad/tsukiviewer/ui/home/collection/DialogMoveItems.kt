package com.flamyoad.tsukiviewer.ui.home.collection

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import com.flamyoad.tsukiviewer.model.DoujinCollection

class DialogMoveItems : DialogFragment() {
    private val viewModel: CollectionDoujinViewModel by activityViewModels()

    private val adapter = CollectionListAdapter(
        viewModel::moveItemsTo,
        this::openNewCollectionDialog
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val layout = layoutInflater.inflate(R.layout.dialog_move_collections, null, false)

        builder.apply {
            setTitle("Move to")
            setView(layout)
            setNegativeButton("Return") { dialog, which->

            }
        }

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.collectionList.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
    }

    private fun openNewCollectionDialog() {
        this.dismiss()

        val newCollectionDialog = DialogNewCollection.newInstance()
        newCollectionDialog.show(childFragmentManager, "DialogNewCollection")
    }

    companion object {
        fun newInstance() = DialogMoveItems()
    }
}