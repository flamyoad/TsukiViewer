package com.flamyoad.tsukiviewer.ui.home.collections

import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionInfoDirectoryAdapter
import com.flamyoad.tsukiviewer.adapter.CollectionInfoTagAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.flamyoad.tsukiviewer.databinding.DialogCollectionInfoBinding

class DialogCollectionInfo : DialogFragment() {
    private var _binding: DialogCollectionInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DialogCollectionInfoViewModel by activityViewModels()

    private val includedTagsAdapter = CollectionInfoTagAdapter(DialogTagPicker.Mode.Inclusive)
    private val excludedTagsAdapter = CollectionInfoTagAdapter(DialogTagPicker.Mode.Exclusive)
    private val dirAdapter = CollectionInfoDirectoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = DialogCollectionInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val window: Window? = dialog?.window ?: return

        val size = Point()
        val display: Display? = window?.windowManager?.defaultDisplay
        display?.getSize(size)

        val params = window?.attributes ?: return
        params.width = (size.x * 0.75).toInt()
//        params.height = (size.y * 0.75).toInt()

        window.attributes = params
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val collectionId = arguments?.getLong(COLLECTION_ID) ?: -1

        viewModel.initCollectionInfo(collectionId)

        viewModel.currentCollection().observe(this, Observer {
            binding.txtCollectionName.text = it.name
            binding.txtMinimumPages.text = when (it.minNumPages == Int.MIN_VALUE) {
                true -> "Not specified"
                false -> it.minNumPages.toString()
            }

            binding.txtMaximumPages.text = when (it.maxNumPages == Int.MAX_VALUE) {
                true -> "Not specified"
                false -> it.maxNumPages.toString()
            }
        })

        viewModel.titles().observe(this, Observer {
            if (it.isEmpty()) {
                binding.txtTitles.text = "Not specified"
            } else {
                binding.txtTitles.text = it.joinToString(", ") { it }
            }
        })

        binding.listIncludedTags.apply {
            layoutManager = FlexboxLayoutManager(this@DialogCollectionInfo.requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            adapter = includedTagsAdapter
        }

        binding.listExcludedTags.apply {
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

        binding.listDirectories.apply {
            adapter = dirAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.dirList().observe(viewLifecycleOwner, Observer {
            dirAdapter.setList(it)
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