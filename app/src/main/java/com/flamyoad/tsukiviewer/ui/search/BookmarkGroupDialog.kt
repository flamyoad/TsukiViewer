package com.flamyoad.tsukiviewer.ui.search

import android.app.Dialog
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
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.ui.doujinpage.BookmarkGroupDialogListener
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogCollectionList
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogNewCollection

class BookmarkGroupDialog : DialogFragment(), BookmarkGroupDialogListener {
    companion object {
        fun newInstance() = BookmarkGroupDialog()
    }

    private val viewModel: SearchResultViewModel by activityViewModels()

    private val collectionAdapter: CollectionPickerAdapter = CollectionPickerAdapter(this)

    private val collectionTickStatus = hashMapOf<BookmarkGroup, Boolean>()

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

        viewModel.bookmarkGroupList.observe(this, Observer {
            collectionAdapter.setList(it)
        })

        setRecyclerviewSize()

        btnSave.setOnClickListener {
            val bookmarkGroups = collectionTickStatus
                .filter { x -> x.value == true }
                .map { x -> x.key }

            viewModel.insertItemIntoTickedCollections(bookmarkGroups)
            this.dismiss()
        }

        btnCancel.setOnClickListener {
            this.dismiss()
        }

        listCollections.adapter = collectionAdapter
        listCollections.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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

    override fun onBookmarkGroupTicked(collection: BookmarkGroup) {
        collectionTickStatus.put(collection, true)
    }

    override fun onBookmarkGroupUnticked(collection: BookmarkGroup) {
        collectionTickStatus.put(collection, false)
    }

    override fun onAddBookmarkGroup() {
        this.dismiss()

        val dialog = DialogNewCollection()
        dialog.show(parentFragmentManager, DialogCollectionList.NEW_COLLECTION_DIALOG)
    }


}