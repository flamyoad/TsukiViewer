package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.TagPickerAdapter
import com.flamyoad.tsukiviewer.ui.search.TagSelectedListener

class DialogTagPicker: DialogFragment() {
    enum class Mode {
        Inclusive,
        Exclusive,
        None
    }

    private val viewModel: CreateCollectionViewModel by activityViewModels()
    private lateinit var listTags: RecyclerView
    private lateinit var fieldTag: EditText

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, fieldTag.text.toString())
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

        fieldTag.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setQuery(text.toString())
            }
        })

        if (savedInstanceState != null) {
            val query = savedInstanceState.getString(SEARCH_QUERY)
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

    companion object {
        const val NAME = "dialog_tag_picker"
        const val SEARCH_QUERY = "dialog_tag_picker_search_query"

        fun newInstance() = DialogTagPicker()
    }
}
