package com.flamyoad.tsukiviewer.ui.home.collections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import com.flamyoad.tsukiviewer.R

class CollectionFragment : Fragment() {
    private val viewModel: CollectionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    companion object {
        fun newInstance() = CollectionFragment()
    }

}
