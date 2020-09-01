package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionPickerAdapter
import com.flamyoad.tsukiviewer.model.DoujinCollection

class DoujinCollectionDialog: DialogFragment(), CollectionDialogListener {

    private val viewmodel: DoujinViewModel by activityViewModels()

    companion object {
        @JvmStatic val DEFAULT_COLLECTION_NAME = "Default Collection"
    }

    private val collectionAdapter: CollectionPickerAdapter = CollectionPickerAdapter(this)

    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var listCollections: RecyclerView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Bookmark to")

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_select_favourite_group, null, false)

        dialogBuilder.setView(view)

        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        listCollections = view.findViewById(R.id.listCollections)

        return dialogBuilder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewmodel.createDefaultCollection()

        viewmodel.initCollectionList()

        viewmodel.collectionList().observe(this, Observer {
            collectionAdapter.setList(it)
        })

        setRecyclerviewSize()

        btnSave.setOnClickListener {
            viewmodel.insertItemIntoTickedCollections()
            this.dismiss()
        }

        btnCancel.setOnClickListener {
            this.dismiss()
        }

        listCollections.adapter = collectionAdapter
        listCollections.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    private fun setRecyclerviewSize() {
        // Set height and width of recyclerview to fixed size
        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display? = window?.getWindowManager()?.getDefaultDisplay()
        display?.getSize(size)

        listCollections.apply {
            layoutParams.height = (size.y * 0.5).toInt()
            requestLayout()
        }
    }

    override fun onCollectionTicked(collection: DoujinCollection) {
        viewmodel.tickCollection(collection)
    }

    override fun onCollectionUnticked(collection: DoujinCollection) {
        viewmodel.untickCollection(collection)
    }
}