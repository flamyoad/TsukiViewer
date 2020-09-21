package com.flamyoad.tsukiviewer.ui.home.collection

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinCollectionAdapter
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.ui.doujinpage.CollectionListDialog
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogNewCollection
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_favourite_doujin.*

class CollectionDoujinFragment : BaseFragment(), ActionMode.Callback, ActionModeListener {

    private val viewmodel: CollectionDoujinViewModel by activityViewModels()

    private val adapter: DoujinCollectionAdapter = DoujinCollectionAdapter(this)

    private var actionMode: ActionMode? = null

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

        viewmodel.headers.observe(viewLifecycleOwner, Observer {
            viewmodel.refreshList()
        })

        viewmodel.itemsNoHeaders.observe(viewLifecycleOwner, Observer {
            viewmodel.refreshList()
        })

        viewmodel.itemsWithHeaders().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    private fun initUi() {
        floatingActionButton.setOnClickListener {
            val dialog = DialogNewCollection()
            dialog.show(parentFragmentManager, CollectionListDialog.NEW_COLLECTION_DIALOG)
        }
    }

    private fun initRecyclerView() {
        adapter.setHasStableIds(true)

        // StateRestorationPolicy is in alpha stage. It may crash the app
        adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    DoujinCollectionAdapter.HEADER_TYPE -> spanCount
                    DoujinCollectionAdapter.ITEM_TYPE -> 1
                    DoujinCollectionAdapter.EMPTY -> spanCount
                    else -> throw IllegalArgumentException("Item view type does not exist")
                }
            }
        }

        val itemDecoration = GridItemDecoration(2, 4, includeEdge = true)

        listCollectionDoujins.adapter = adapter
        listCollectionDoujins.layoutManager = gridLayoutManager
        listCollectionDoujins.addItemDecoration(itemDecoration)
        listCollectionDoujins.setHasFixedSize(true)
        listCollectionDoujins.itemAnimator = null
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

        builder.apply {
            setTitle(item.collectionName)
            setMessage("Are you sure you want to delete this collection? Existing items will be lost")
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                viewmodel.deleteCollection(item.collectionName)
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->

            })
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteDialog(mode: ActionMode?) {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle("Delete ${adapter.getSelectedItemCount()} items?")
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                // toList() is required.
                // This is because the referenced list is cleared automatically in destruction of dialog
                // If toList() is not called, delete will not work because the referenced list is already empty
                viewmodel.deleteItems(adapter.selectedItems.toList())
                mode?.finish()
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            setItems(
                adapter.getSelectedItemNames(),
                DialogInterface.OnClickListener { dialogInterface, i ->

                })
        }

        val dialog = builder.create()

        dialog.listView.setOnItemClickListener { adapterView, view, i, l ->
            // Does nothing. Replaces the default listener just to prevent
            // the dialog from closing itself when clicking on one of the items
        }

        dialog.show()
    }

    override fun openActionMode() {
        if (requireActivity() is AppCompatActivity) {
            val appCompat = requireActivity() as AppCompatActivity
            actionMode = appCompat.startSupportActionMode(this)
            adapter.setActionMode(true)
        }
    }

    override fun onItemCountChange(count: Int) {
        if (count == 0) {
            actionMode?.finish()
        }
        actionMode?.setTitle(count.toString())
        actionMode?.invalidate()
    }

    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?
    ): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> showDeleteDialog(mode)
        }
        return true
    }

    override fun onCreateActionMode(
        mode: ActionMode?,
        menu: Menu?
    ): Boolean {
        requireActivity().menuInflater.inflate(R.menu.menu_contextual_collection, menu)
        return true
    }

    override fun onPrepareActionMode(
        mode: ActionMode?,
        menu: Menu?
    ): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        adapter.setActionMode(false)
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    companion object {
        const val MENU_CHANGE_NAME = "Change Name"
        const val MENU_DELETE_COLLECTION = "Delete Collection"

        const val CHANGE_NAME_DIALOG = "change_name_dialog"
        const val DELETE_COLLECTION_DIALOG = "delete_collection_dialog"

        const val APPBAR_TITLE = "Collections"

        @JvmStatic
        fun newInstance() =
            CollectionDoujinFragment()
    }

}
