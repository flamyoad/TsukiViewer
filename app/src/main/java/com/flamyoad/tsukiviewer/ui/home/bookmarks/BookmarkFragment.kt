package com.flamyoad.tsukiviewer.ui.home.bookmarks

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.BaseFragment

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.BookmarkGroupAdapter
import com.flamyoad.tsukiviewer.adapter.BookmarkItemsAdapter
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.BookmarkItem
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_bookmark.*
import java.util.*

private const val ACTION_MODE = "action_mode"
private const val SEARCH_VIEW = "search_view"

class BookmarkFragment : BaseFragment(),
    ActionModeListener<BookmarkItem>, SearchView.OnQueryTextListener {

    private val viewModel: BookmarkViewModel by activityViewModels()
    private val groupAdapter = BookmarkGroupAdapter(this::onGroupChange, this::showNewGroupDialog)
    private val itemAdapter = BookmarkItemsAdapter(this, false)
    private var actionMode: ActionMode? = null

    private var searchView: SearchView? = null
    private var statusBarColor: Int = -1
    private var previousSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        previousSearchQuery = savedInstanceState?.getString(SEARCH_VIEW) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val isInActionMode = actionMode != null
        outState.putBoolean(ACTION_MODE, isInActionMode)
        outState.putString(SEARCH_VIEW, searchView?.query.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_doujin_collection, menu)

        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        if (previousSearchQuery.isNotBlank()) {
            searchItem.expandActionView()
            searchView.setQuery(previousSearchQuery, false)
            searchView.clearFocus()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)

        val progressBar = menu.findItem(R.id.progress_bar_loading)

        viewModel.isLoading().observe(viewLifecycleOwner, Observer { stillLoading ->
            if (stillLoading) {
                progressBar.isVisible = true
                searchItem.isVisible = false
            } else {
                progressBar.isVisible = false
                searchItem.isVisible = true
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            // Restores action mode
            val shouldRestartActionMode = savedInstanceState.getBoolean(ACTION_MODE, false)
            if (shouldRestartActionMode) {
                startActionMode()
                actionMode?.title = viewModel.selectedBookmarkCount().toString() + " selected"
            }
        }

        initBookmarkGroups()
        initBookmarkItems()
        initUi()

        viewModel.groupList.observe(viewLifecycleOwner, Observer {
            viewModel.initializeGroups(it)
        })

        viewModel.bookmarkGroups().observe(viewLifecycleOwner, Observer {
            groupAdapter.submitList(it)
            btnContextMenu.visibility = View.VISIBLE

            // If all bookmarks have been removed by user
            if (it.isNullOrEmpty()) {
                header.text = "No bookmark exists"
                btnContextMenu.visibility = View.GONE
            }
        })

        viewModel.selectedBookmarkGroup().observe(viewLifecycleOwner, Observer {
            viewModel.fetchBookmarkItems(it)
            header.text = it.name
        })

        viewModel.bookmarkItems.observe(viewLifecycleOwner, Observer {
            viewModel.selectedGroup?.let {
                viewModel.fetchBookmarkItems(it)
                viewModel.refreshGroupInfo()
            }
        })

        viewModel.processedBookmarks().observe(viewLifecycleOwner, Observer {
            itemAdapter.submitList(it.toList())
        })
    }

    private fun initBookmarkGroups() {
        groupAdapter.setHasStableIds(true)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        layoutManager.isSmoothScrollbarEnabled = true

        val linearSnapHelper = LinearSnapHelper()

        listGroups.adapter = groupAdapter
        listGroups.layoutManager = layoutManager
        linearSnapHelper.attachToRecyclerView(listGroups)
    }

    private fun initBookmarkItems() {
        itemAdapter.setHasStableIds(true)

        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)

        val itemDecoration = GridItemDecoration(2, 4, includeEdge = true)

        listItems.adapter = itemAdapter
        listItems.layoutManager = gridLayoutManager
        listItems.addItemDecoration(itemDecoration)
        listItems.setHasFixedSize(true)
        listItems.itemAnimator = null
    }

    private fun initUi() {
        registerForContextMenu(btnContextMenu)
        btnContextMenu.setOnClickListener {
            it.showContextMenu()
        }

        fab.setOnClickListener {
            showNewGroupDialog()
        }
    }

    private fun onGroupChange(group: BookmarkGroup) {
        viewModel.switchBookmarkGroup(group)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val selectedGroup = viewModel.selectedGroup

        menu.setHeaderTitle(selectedGroup?.name ?: "Menu")
        menu.add(MENU_CHANGE_NAME)
        menu.add(MENU_DELETE_COLLECTION)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val selectedCollection = viewModel.selectedGroup ?: return true

        when (item.title) {
            MENU_CHANGE_NAME -> showChangeNameDialog(selectedCollection)
            MENU_DELETE_COLLECTION -> showDeleteGroupDialog(selectedCollection)
        }
        return true
    }

    private fun showNewGroupDialog() {
        val dialog = DialogNewGroup.newInstance()
        dialog.show(childFragmentManager, DIALOG_NEW_GROUP)
    }

    private fun showChangeNameDialog(group: BookmarkGroup) {
        val dialog = DialogChangeGroupName.newInstance(group.name)
        dialog.show(childFragmentManager, DIALOG_CHANGE_NAME)
    }

    private fun showDeleteGroupDialog(group: BookmarkGroup) {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle(group.name)
            setMessage("Are you sure you want to delete this collection? Existing items will be lost")
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                viewModel.deleteGroup(group)
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->
            })
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteItemsDialog(mode: ActionMode?) {
        val builder = AlertDialog.Builder(requireContext())

        val count = viewModel.selectedBookmarkCount()
        val title = if (count > 1)
            "Remove $count bookmarks?"
        else
            "Remove $count bookmark?"

        builder.apply {
            setTitle(title)
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                viewModel.deleteItems()
                mode?.finish()
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            setItems(
                viewModel.selectedBookmarkNames(),
                DialogInterface.OnClickListener { dialogInterface, i -> })
        }

        val dialog = builder.create()

        dialog.listView.setOnItemClickListener { adapterView, view, i, l ->
            // Does nothing. Replaces the default listener just to prevent the
            // dialog from closing itself when clicking on one of the items
        }

        dialog.show()
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    companion object {
        const val MENU_CHANGE_NAME = "Change Name"
        const val MENU_DELETE_COLLECTION = "Delete Collection"

        const val DIALOG_NEW_GROUP = "new_group_dialog"
        const val DIALOG_CHANGE_NAME = "change_name_dialog"
        const val DIALOG_DELETE_GROUP = "delete_group_dialog"

        const val APPBAR_TITLE = "Bookmarks"

        @JvmStatic
        fun newInstance() =
            BookmarkFragment()
    }

    override fun startActionMode() {
        if (requireActivity() is AppCompatActivity) {
            val appCompat = requireActivity() as AppCompatActivity
            actionMode = appCompat.startSupportActionMode(ActionModeCallback())
            itemAdapter.actionModeEnabled = true
        }
    }

    override fun onMultiSelectionClick(item: BookmarkItem) {
        viewModel.tickSelectedBookmark(item)

        val count = viewModel.selectedBookmarkCount()
        if (count == 0) {
            actionMode?.finish()
            viewModel.clearSelectedBookmarks()
        }

        actionMode?.title = count.toString() + " selected"
        actionMode?.invalidate()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val correctedText = newText
            ?.trim()
            ?.toLowerCase(Locale.ROOT) ?: ""

        viewModel.filterList(correctedText)
        return true
    }

    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_delete -> showDeleteItemsDialog(mode)
            }

            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().let {
                it.menuInflater.inflate(R.menu.menu_doujin_collection_contextual, menu)
                statusBarColor = it.window.statusBarColor // Stores the original status bar color

                val colorPrimaryLight = ContextCompat.getColor(it, R.color.colorPrimaryLight)
                it.window.statusBarColor =
                    colorPrimaryLight // Changes status bar color in action mode
            }

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        // This method is not called on screen rotation
        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            requireActivity().window.statusBarColor = statusBarColor // Restores the original status bar color

            itemAdapter.actionModeEnabled = false

            viewModel.clearSelectedBookmarks()
        }

    }
}