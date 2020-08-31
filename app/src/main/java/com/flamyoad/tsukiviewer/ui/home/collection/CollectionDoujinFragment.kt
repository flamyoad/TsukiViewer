package com.flamyoad.tsukiviewer.ui.home.collection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinCollectionAdapter
import com.flamyoad.tsukiviewer.utils.GridSpacingItemDecoration
import com.flamyoad.tsukiviewer.utils.ItemOffsetDecoration
import kotlinx.android.synthetic.main.fragment_favourite_doujin.*
import java.lang.IllegalArgumentException

class CollectionDoujinFragment : Fragment() {

    private val viewmodel: CollectionDoujinViewModel by activityViewModels()

    private val adapter: DoujinCollectionAdapter = DoujinCollectionAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_doujin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()

        viewmodel.itemsNoHeaders.observe(viewLifecycleOwner, Observer {
            viewmodel.refreshList()
        })

        viewmodel.itemsWithHeaders().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
    }

    private fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    DoujinCollectionAdapter.HEADER_TYPE -> 2
                    DoujinCollectionAdapter.ITEM_TYPE -> 1
                    DoujinCollectionAdapter.EMPTY -> 2
                    else -> throw IllegalArgumentException("Item view type does not exist")
                }
            }
        }

        val itemDecoration = GridSpacingItemDecoration(2, 4, includeEdge = true)

        listCollectionDoujins.adapter = adapter
        listCollectionDoujins.layoutManager = gridLayoutManager
        listCollectionDoujins.addItemDecoration(itemDecoration)
        listCollectionDoujins.setHasFixedSize(true)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CollectionDoujinFragment()
    }
}
