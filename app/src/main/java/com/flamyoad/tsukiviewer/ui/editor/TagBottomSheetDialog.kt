package com.flamyoad.tsukiviewer.ui.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.EditorNewTagAdapter
import com.flamyoad.tsukiviewer.databinding.EditorNewTagBottomsheetBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TagBottomSheetDialog()
    : BottomSheetDialogFragment() {

    private var _binding: EditorNewTagBottomsheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewmodel: EditorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditorNewTagBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(EditorViewModel::class.java)

        viewmodel.selectedCategory().observe(viewLifecycleOwner, Observer { category ->
            binding.lblCategory.text = category.capitalize()
        })
        initTagList()
    }

    private fun initTagList() {
        val listener = requireActivity() as CreateTagListener
        val adapter = EditorNewTagAdapter(listener)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.listTags.adapter = adapter
        binding.listTags.layoutManager = linearLayoutManager

        viewmodel.tagsByCategory.observe(this, Observer {
            adapter.setList(it)
        })

        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.addFilter(text.toString())
            }
        })

        binding.btnInsertTag.setOnClickListener {
            val tagName = binding.inputEditText.text.toString()
            val category = viewmodel.selectedCategory().value!!
            listener.onTagCreated(tagName, category)
        }
    }
}