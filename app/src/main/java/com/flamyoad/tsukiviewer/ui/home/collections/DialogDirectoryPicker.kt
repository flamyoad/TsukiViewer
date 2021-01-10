package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DirectoryPickerAdapter
import java.io.File

class DialogDirectoryPicker: DialogFragment() {

    private val viewModel: CreateCollectionViewModel by activityViewModels()

    private lateinit var listDirs: RecyclerView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pick a directory")

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_collection_directory_picker, null, false)

        listDirs = view.findViewById(R.id.listDirs)

        builder.setView(view)

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerviewSize()

        val dirAdapter = DirectoryPickerAdapter(this::addDir)

        listDirs.adapter = dirAdapter
        listDirs.layoutManager = LinearLayoutManager(requireContext())

        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        )
        listDirs.addItemDecoration(dividerItemDecoration)

        viewModel.includedPaths.observe(this, Observer {
            val dirs = it.map { x -> x.dir }
            dirAdapter.setList(dirs)
        })
    }

    private fun setRecyclerviewSize() {
        // Set height and width of recyclerview to fixed size
        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display? = window?.getWindowManager()?.getDefaultDisplay()
        display?.getSize(size)

        listDirs.apply {
            layoutParams.height = (size.y * 0.65).toInt()
            requestLayout()
        }
    }

    private fun addDir(dir: File) {
        viewModel.addDir(dir)
        this.dismiss()
    }

    companion object {
        const val NAME = "dialog_directory_picker"

        fun newInstance() = DialogDirectoryPicker()
    }
}