package com.flamyoad.tsukiviewer.ui.home.collection

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinCollectionAdapter
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.ui.doujinpage.CollectionListDialog
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogNewCollection
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_favourite_doujin.*

private const val ACTION_MODE = "actionmode"

class CollectionDoujinFragment : BaseFragment(),
    ActionMode.Callback,
    ActionModeListener,
    ToggleHeaderListener,
    SearchView.OnQueryTextListener {

    private val viewModel: CollectionDoujinViewModel by activityViewModels()

    private val adapter: DoujinCollectionAdapter = DoujinCollectionAdapter(this, this, false)

    private var actionMode: ActionMode? = null

    private var searchView: SearchView? = null

    private var statusBarColor: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isInActionMode = actionMode != null
        outState.putBoolean(ACTION_MODE, isInActionMode)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_doujin_collection, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)

        val progressBar = menu.findItem(R.id.progress_bar_loading)

        viewModel.isLoading().observe(viewLifecycleOwner, Observer {  stillLoading ->
            if (stillLoading) {
                progressBar.isVisible = true
                searchItem.isVisible = false
            } else {
                progressBar.isVisible = false
                searchItem.isVisible = true
            }
        })
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

        if (savedInstanceState != null) {
            val shouldRestartActionMode = savedInstanceState.getBoolean(ACTION_MODE, false)
            if (shouldRestartActionMode) {
                startActionMode()
                actionMode?.title = viewModel.selectedItemsList.size.toString()
            }
        }

        initUi()
        initRecyclerView()

        registerForContextMenu(listCollectionDoujins)

        viewModel.allItems.observe(viewLifecycleOwner, Observer {
            viewModel.initList()
        })

        viewModel.itemsWithHeaders().observe(viewLifecycleOwner, Observer {
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
            MENU_CHANGE_NAME -> showChangeNameDialog(selectedCollection)
            MENU_DELETE_COLLECTION -> showDeleteCollectionDialog(selectedCollection)
        }
        return true
    }

    private fun showChangeNameDialog(item: CollectionItem) {
        val dialog = DialogChangeName.newInstance(item.collectionName)
        dialog.show(parentFragmentManager, CHANGE_NAME_DIALOG)
    }

    private fun showDeleteCollectionDialog(item: CollectionItem) {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle(item.collectionName)
            setMessage("Are you sure you want to delete this collection? Existing items will be lost")
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                viewModel.deleteCollection(item.collectionName)
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->
            })
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteItemsDialog(mode: ActionMode?) {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle("Delete ${viewModel.getSelectedItemCount()} items?")
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                viewModel.deleteItems()
                mode?.finish()
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            setItems(
                viewModel.getSelectedItemNames(),
                DialogInterface.OnClickListener { dialogInterface, i -> })
        }

        val dialog = builder.create()

        dialog.listView.setOnItemClickListener { adapterView, view, i, l ->
            // Does nothing. Replaces the default listener just to prevent the
            // dialog from closing itself when clicking on one of the items
        }

        dialog.show()
    }

    override fun startActionMode() {
        if (requireActivity() is AppCompatActivity) {
            val appCompat = requireActivity() as AppCompatActivity
            actionMode = appCompat.startSupportActionMode(this)
            adapter.actionModeEnabled = true
        }
    }

    override fun onMultiSelectionClick(item: CollectionItem) {
        viewModel.onItemSelected(item)

        val count = viewModel.selectedItemsList.size
        if (count == 0) {
            actionMode?.finish()
        }

        actionMode?.title = count.toString()
        actionMode?.invalidate()
    }

    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?
    ): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> showDeleteItemsDialog(mode)
        }

        return true
    }

    override fun onCreateActionMode(
        mode: ActionMode?,
        menu: Menu?
    ): Boolean {
        val activity = requireActivity()

        activity.let {
            it.menuInflater.inflate(R.menu.menu_doujin_collection_contextual, menu)
            statusBarColor = it.window.statusBarColor // Stores the original status bar color

            val colorPrimaryLight = ContextCompat.getColor(it, R.color.colorPrimaryLight)
            it.window.statusBarColor = colorPrimaryLight // Changes status bar color in action mode
        }

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

        viewModel.clearActionModeData()
        adapter.actionModeEnabled = false

        requireActivity().window.statusBarColor = statusBarColor // Restores the original status bar color
    }

    override fun toggleHeader(header: CollectionItem, headerPosition: Int) {
        viewModel.toggleHeader(header, headerPosition)
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
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
