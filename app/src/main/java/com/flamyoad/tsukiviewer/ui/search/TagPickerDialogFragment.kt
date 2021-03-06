package com.flamyoad.tsukiviewer.ui.search

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.TagPickerAdapter

private const val TAG_SEARCH_QUERY = "TAG_SEARCH_QUERY"

class TagPickerDialogFragment()
    : DialogFragment() {

    private lateinit var viewModel: SearchViewModel

    private lateinit var listTags: RecyclerView
    private lateinit var fieldTag: EditText

    companion object {
        fun newInstance(): TagPickerDialogFragment {
            return TagPickerDialogFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TAG_SEARCH_QUERY, fieldTag.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dismiss()
            })

        val view = layoutInflater.inflate(R.layout.tag_picker_dialog, null, false)

        listTags = view.findViewById(R.id.listTags)
        fieldTag = view.findViewById(R.id.fieldTag)

        builder.setView(view)

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        fieldTag.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setQuery(text.toString())
            }
        })

        if (savedInstanceState != null) {
            val query = savedInstanceState.getString(TAG_SEARCH_QUERY)
            if (query != null) {
                fieldTag.setText(query)
            }
        }

        val adapter = TagPickerAdapter(requireActivity() as TagSelectedListener)

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listTags.adapter = adapter
        listTags.layoutManager = linearLayoutManager

        viewModel.tagList.observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.clearQuery()
    }
}
