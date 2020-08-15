package com.flamyoad.tsukiviewer.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.TagPickerAdapter
import com.flamyoad.tsukiviewer.model.Tag
import kotlinx.android.synthetic.main.tag_picker_dialog.*

class TagPickerDialogFragment()
    : DialogFragment() {

    private lateinit var viewmodel: SearchViewModel

    private val adapter = TagPickerAdapter()

    companion object {
        fun newInstance(): TagPickerDialogFragment {
            return TagPickerDialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tag_picker_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listTags.adapter = adapter
        listTags.layoutManager = linearLayoutManager
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewmodel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        viewmodel.tagList.observe(this, Observer {
            adapter.setList(it)
        })
    }



}