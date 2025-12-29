package com.flamyoad.tsukiviewer.ui.doujinpage

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
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import javax.inject.Inject

class DialogViewStyle : DialogFragment() {
    
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: DoujinViewModel by activityViewModels { viewModelFactory }

    private lateinit var btnGrid: TextView
    private lateinit var btnScaled: TextView
    private lateinit var btnRow: TextView
    private lateinit var btnList: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_grid_view_style, null, false)

        builder.setView(view)

        btnGrid = view.findViewById(R.id.btnGrid)
        btnScaled = view.findViewById(R.id.btnScaled)
        btnRow = view.findViewById(R.id.btnRow)
        btnList = view.findViewById(R.id.btnList)

        btnGrid.setOnClickListener { switchViewStyle(GridViewStyle.Grid) }
        btnScaled.setOnClickListener { switchViewStyle(GridViewStyle.Scaled) }
        btnRow.setOnClickListener { switchViewStyle(GridViewStyle.Row) }
        btnList.setOnClickListener { switchViewStyle(GridViewStyle.List)}

        val dialog = builder.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // Removes the unused white title bar

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val selectedColor = ContextCompat.getColor(requireContext(), R.color.grid_item_selected_color)

        viewModel.gridViewStyle().observe(this, Observer { style ->
            when (style) {
                GridViewStyle.Grid -> btnGrid.setBackgroundColor(selectedColor)
                GridViewStyle.Scaled -> btnScaled.setBackgroundColor(selectedColor)
                GridViewStyle.Row -> btnRow.setBackgroundColor(selectedColor)
                GridViewStyle.List -> btnList.setBackgroundColor(selectedColor)

                else -> return@Observer
            }
        })
    }

    private fun switchViewStyle(style: GridViewStyle) {
        if (style == GridViewStyle.None)
            return

        viewModel.switchViewStyle(style)
        dismiss()
    }

    companion object {
        fun newInstance() = DialogViewStyle()
    }
}