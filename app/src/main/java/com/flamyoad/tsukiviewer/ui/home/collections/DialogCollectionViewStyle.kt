package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.ui.doujinpage.GridViewStyle
import javax.inject.Inject

class DialogCollectionViewStyle: DialogFragment() {
    private lateinit var btnGrid: TextView
    private lateinit var btnList: TextView

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: CollectionViewModel by activityViewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_collection_view_style, null, false)

        builder.setView(view)

        btnGrid = view.findViewById(R.id.btnGrid)
        btnList = view.findViewById(R.id.btnList)

        btnGrid.setOnClickListener { switchViewStyle(CollectionListAdapter.GRID) }
        btnList.setOnClickListener { switchViewStyle(CollectionListAdapter.LIST) }

        val dialog = builder.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // Removes the unused white title bar

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val selectedColor = ContextCompat.getColor(requireContext(), R.color.grid_item_selected_color)

        viewModel.collectionViewStyle().observe(this, Observer { style ->
            when (style) {
                CollectionListAdapter.GRID -> btnGrid.setBackgroundColor(selectedColor)
                CollectionListAdapter.LIST -> btnList.setBackgroundColor(selectedColor)

                else -> return@Observer
            }
        })
    }

    private fun switchViewStyle(style: Int) {
        viewModel.switchViewStyle(style)
        dismiss()
    }

    companion object {
        val NAME = "DialogCollectionViewStyle"
        fun newInstance() = DialogCollectionViewStyle()
    }
}