package com.flamyoad.tsukiviewer.ui.home.collection

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinCollectionAdapter
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.ui.doujinpage.CollectionListDialog
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogNewCollection
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
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

        initUi()
        initRecyclerView()

        registerForContextMenu(listCollectionDoujins)

        viewmodel.itemsNoHeaders.observe(viewLifecycleOwner, Observer {
            viewmodel.refreshList()
        })

        viewmodel.itemsWithHeaders().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
    }

    private fun initUi() {
        floatingActionButton.setOnClickListener {
            val dialog = DialogNewCollection()
            dialog.show(parentFragmentManager, CollectionListDialog.NEW_COLLECTION_DIALOG)
        }
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

        val itemDecoration = GridItemDecoration(2, 4, includeEdge = true)

        listCollectionDoujins.adapter = adapter
        listCollectionDoujins.layoutManager = gridLayoutManager
        listCollectionDoujins.addItemDecoration(itemDecoration)
        listCollectionDoujins.setHasFixedSize(true)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val collectionName = adapter.onLongClickItem?.collectionName ?: ""

        menu.setHeaderTitle(collectionName)
        menu.add(MENU_CHANGE_NAME)
        menu.add(MENU_DELETE_COLLECTION)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val selectedCollection = adapter.onLongClickItem ?: return true

        when (item.title) {
            MENU_CHANGE_NAME -> openChangeNameDialog(selectedCollection)
            MENU_DELETE_COLLECTION -> openDeleteCollectionDialog(selectedCollection)
        }
        return true
    }

    private fun openChangeNameDialog(item: CollectionItem) {
        val dialog = DialogChangeName.newInstance(item.collectionName)
        dialog.show(parentFragmentManager, CHANGE_NAME_DIALOG)
    }

    private fun openDeleteCollectionDialog(item: CollectionItem) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(item.collectionName)
        builder.setMessage("Are you sure you want to delete this collection? Existing items will be lost")

        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
            viewmodel.deleteCollection(item.collectionName)
        })

        builder.setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->

        })

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        const val MENU_CHANGE_NAME = "Change Name"
        const val MENU_DELETE_COLLECTION = "Delete Collection"

        const val CHANGE_NAME_DIALOG = "change_name_dialog"
        const val DELETE_COLLECTION_DIALOG = "delete_collection_dialog"

        @JvmStatic
        fun newInstance() =
            CollectionDoujinFragment()
    }
}
