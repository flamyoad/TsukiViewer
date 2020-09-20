package com.flamyoad.tsukiviewer.ui.home.tags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinTagsAdapter
import com.flamyoad.tsukiviewer.model.TagType
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_tag.*

private const val TAG_NAME = "tag_name"

class TagFragment : Fragment() {

    private val viewModel by activityViewModels<DoujinTagsViewModel>()

    private val adapter = DoujinTagsAdapter(useLargerView = true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tag, container, false)
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
        listTags.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listTags.layoutManager = linearLayoutManager
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
