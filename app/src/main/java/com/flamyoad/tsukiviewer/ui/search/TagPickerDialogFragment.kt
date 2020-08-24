package com.flamyoad.tsukiviewer.ui.search

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Display
import android.view.Gravity
import android.view.Window
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.TagPickerAdapter


class TagPickerDialogFragment()
    : DialogFragment() {

    private lateinit var viewmodel: SearchViewModel

    private lateinit var listTags: RecyclerView

    private lateinit var fieldTag: EditText

    companion object {
        fun newInstance(): TagPickerDialogFragment {
            return TagPickerDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dismiss()
            })

        val view = layoutInflater.inflate(R.layout.tag_picker_dialog, null)

        listTags = view.findViewById(R.id.listTags)
        fieldTag = view.findViewById(R.id.fieldTag)

        builder.setView(view)

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewmodel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        val tagSelectedListener = requireActivity() as TagSelectedListener

        val adapter = TagPickerAdapter(tagSelectedListener)

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listTags.adapter = adapter
        listTags.layoutManager = linearLayoutManager

        fieldTag.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.addFilter(text.toString())
            }
        })

        viewmodel.tagList.observe(this, Observer {
            adapter.setList(it)
        })
    }
}
