package com.flamyoad.tsukiviewer.ui.home.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionInfoTagAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.dialog_collection_info.*

class DialogCollectionInfo : DialogFragment() {
    private val viewModel: DialogCollectionInfoViewModel by activityViewModels()

    private val includedTagsAdapter = CollectionInfoTagAdapter(DialogTagPicker.Mode.Inclusive)
    private val excludedTagsAdapter = CollectionInfoTagAdapter(DialogTagPicker.Mode.Exclusive)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_collection_info, null, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val collectionId = arguments?.getLong(COLLECTION_ID) ?: -1

        viewModel.initCollectionInfo(collectionId)

        viewModel.currentCollection().observe(this, Observer {
            txtCollectionName.text = it.name
            txtMinimumPages.text = when (it.minNumPages == Int.MIN_VALUE) {
                true -> "Not specified"
                false -> it.minNumPages.toString()
            }

            txtMaximumPages.text = when (it.maxNumPages == Int.MAX_VALUE) {
                true -> "Not specified"
                false -> it.maxNumPages.toString()
            }
        })

        viewModel.titles().observe(this, Observer {
            if (it.isEmpty()) {
                txtTitles.text = "Not specified"
            } else {
                txtTitles.text = it.joinToString(", ") { it }
            }
        })

        listIncludedTags.apply {
            layoutManager = FlexboxLayoutManager(this@DialogCollectionInfo.requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            adapter = includedTagsAdapter
        }

        listExcludedTags.apply {
            layoutManager = FlexboxLayoutManager(this@DialogCollectionInfo.requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            adapter = excludedTagsAdapter
        }

        viewModel.includedTags().observe(viewLifecycleOwner, Observer {
            includedTagsAdapter.setList(it)
        })

        viewModel.excludedTags().observe(viewLifecycleOwner, Observer {
            excludedTagsAdapter.setList(it)
        })
    }


    companion object {
        const val NAME = "dialog_collection_info"
        const val COLLECTION_ID = "collection_id"

        fun newInstance(collectionId: Long) =
            DialogCollectionInfo().apply {
                arguments = bundleOf(COLLECTION_ID to collectionId)
            }
    }
}