package com.flamyoad.tsukiviewer.ui.home.collections

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.BaseFragment

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import kotlinx.android.synthetic.main.fragment_collection.*

class CollectionFragment : BaseFragment() {
    private val viewModel: CollectionViewModel by activityViewModels()

    private val collectionAdapter = CollectionListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()

        fab.setOnClickListener {
            val context = requireContext()
            val intent = Intent(context, CreateCollectionActivity::class.java)
            context.startActivity(intent)
        }

        viewModel.collectionWithCriterias.observe(viewLifecycleOwner, Observer {
            collectionAdapter.submitList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_local_doujins, menu)
    }

    private fun initRecyclerView() {
        listCollections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = collectionAdapter
        }
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    companion object {
        const val APPBAR_TITLE = "Collections"
        const val COLLECTION_ID = "collection_id"
        const val COLLECTION_NAME = "collection_name"
        const val COLLECTION_CRITERIAS = "collection_criterias"

        fun newInstance() = CollectionFragment()
    }

}
