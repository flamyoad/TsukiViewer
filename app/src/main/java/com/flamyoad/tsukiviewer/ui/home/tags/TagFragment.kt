package com.flamyoad.tsukiviewer.ui.home.tags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.flamyoad.tsukiviewer.adapter.DoujinTagsAdapter
import com.flamyoad.tsukiviewer.databinding.FragmentTagBinding
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.model.TagType

private const val TAG_NAME = "tag_name"

class TagFragment : Fragment() {

    private var _binding: FragmentTagBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by activityViewModels<DoujinTagsViewModel>()

    private val adapter = DoujinTagsAdapter(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.setListener(this::showDeleteDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initList()

        val bundle = arguments

        bundle?.let {
            val tagType = TagType.valueOf(it.getString(TAG_NAME) ?: "")
            viewModel.getTagItems(tagType).observe(viewLifecycleOwner, Observer {
                adapter.setList(it)
            })
        }
    }

    private fun initList() {
        binding.listTags.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.listTags.layoutManager = linearLayoutManager
    }

    private fun showDeleteDialog(tag: Tag) {
        val dialog = DialogDeleteTag.newInstance(tag)
        dialog.show(childFragmentManager, DialogDeleteTag.name)
    }


    companion object {
        @JvmStatic
        fun newInstance(tagType: TagType) =
            TagFragment().apply {
                arguments = Bundle().apply {
                    putString(TAG_NAME, tagType.toString())
                }
            }
    }
}
