package com.flamyoad.tsukiviewer.ui.home.tags

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinTagsAdapter
import com.flamyoad.tsukiviewer.databinding.FragmentTagBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.core.model.Doujin
import com.flamyoad.tsukiviewer.core.model.Tag
import com.flamyoad.tsukiviewer.core.model.TagType
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import javax.inject.Inject

private const val TAG_NAME = "tag_name"

class TagFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: DoujinTagsViewModel by activityViewModels { viewModelFactory }

    private var _binding: FragmentTagBinding? = null
    private val binding get() = _binding!!

    private val adapter = DoujinTagsAdapter(true)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.setListener(this::showDeleteDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
