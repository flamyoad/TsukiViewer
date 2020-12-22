package com.flamyoad.tsukiviewer.ui.home.collections

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.flamyoad.tsukiviewer.BaseFragment

import com.flamyoad.tsukiviewer.R
import kotlinx.android.synthetic.main.fragment_collection.*

class CollectionFragment : BaseFragment() {
    private val viewModel: CollectionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fab.setOnClickListener {
            val context = requireContext()
            val intent = Intent(context, CreateCollectionActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    companion object {
        const val APPBAR_TITLE = "Collections"
        fun newInstance() = CollectionFragment()
    }

}
