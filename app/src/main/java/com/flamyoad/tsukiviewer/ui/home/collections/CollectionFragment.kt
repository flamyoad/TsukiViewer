package com.flamyoad.tsukiviewer.ui.home.collections

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import com.flamyoad.tsukiviewer.model.Collection
import kotlinx.android.synthetic.main.fragment_collection.*

class CollectionFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private val viewModel: CollectionViewModel by activityViewModels()

    private val collectionAdapter =
        CollectionListAdapter(this::onEditCollection, this::onRemoveCollection, CollectionListAdapter.LIST)

    private var searchView: SearchView? = null
    private var previousSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.initCollectionThumbnail()
    }

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
        inflater.inflate(R.menu.menu_collections, menu)

        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        if (previousSearchQuery.isNotBlank()) {
            searchItem.expandActionView()
            searchView?.setQuery(previousSearchQuery, false)
            searchView?.clearFocus()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    private fun initRecyclerView() {
        listCollections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = collectionAdapter
        }
    }

    private fun onEditCollection(collection: Collection) {

    }

    private fun onRemoveCollection(collection: Collection) {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle(collection.name)
            setMessage("Are you sure you want to delete this collection? Existing items will be lost")
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                viewModel.deleteCollection(collection)
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->
            })
        }

        val dialog = builder.create()
        dialog.show()
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

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterList(newText ?: "")
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

}
