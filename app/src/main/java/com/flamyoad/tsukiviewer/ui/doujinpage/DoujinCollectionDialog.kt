package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import com.flamyoad.tsukiviewer.model.DoujinCollection

class DoujinCollectionDialog: DialogFragment() {

    private val collectionAdapter: CollectionListAdapter = CollectionListAdapter()

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

        setRecyclerviewSize()

        btnSave.setOnClickListener {
            this.dismiss()
        }

        btnCancel.setOnClickListener {
            this.dismiss()
        }

        listCollections.adapter = collectionAdapter
        listCollections.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val mock = listOf(
            DoujinCollection("Default Collection"),
            DoujinCollection("Love Live"),
            DoujinCollection("这啥"),
            DoujinCollection("ラブライブ")
        )

        collectionAdapter.setList(mock)
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
}