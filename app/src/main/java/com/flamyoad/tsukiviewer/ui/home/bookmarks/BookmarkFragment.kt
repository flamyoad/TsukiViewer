package com.flamyoad.tsukiviewer.ui.home.bookmarks

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.BookmarkGroupAdapter
import com.flamyoad.tsukiviewer.adapter.BookmarkItemsAdapter
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.BookmarkItem
import com.flamyoad.tsukiviewer.model.ViewMode
import com.flamyoad.tsukiviewer.utils.ui.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_bookmark.*
import java.util.*

private const val ACTION_MODE = "action_mode"
private const val SEARCH_VIEW = "search_view"

class BookmarkFragment : BaseFragment(),
    DeleteItemsListener,
    ActionModeListener<BookmarkItem>,
    SearchView.OnQueryTextListener {

    private val viewModel: BookmarkViewModel by activityViewModels()
    private val groupAdapter = BookmarkGroupAdapter(this::onGroupChange, this::showNewGroupDialog)
    private val itemAdapter = BookmarkItemsAdapter(false)
        .apply { setHasStableIds(true) }

    private var actionMode: ActionMode? = null
    private var actionModeCallback: ActionMode.Callback? = null

    private var appPreference: MyAppPreference? = null

    private var searchView: SearchView? = null
    private var statusBarColor: Int = -1
    private var previousSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previousSearchQuery = savedInstanceState?.getString(SEARCH_VIEW) ?: ""
        itemAdapter.setListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        itemAdapter.removeListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isInActionMode = actionMode != null
        outState.putBoolean(ACTION_MODE, isInActionMode)
        outState.putString(SEARCH_VIEW, searchView?.query.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_doujin_bookmarks, menu)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_normal_grid -> {
                if (itemAdapter.getViewMode() == ViewMode.NORMAL_GRID) return true
                initBookmarkItems(ViewMode.NORMAL_GRID)
                appPreference?.setDoujinViewMode(ViewMode.NORMAL_GRID)
            }

            R.id.action_view_scaled -> {
                if (itemAdapter.getViewMode() == ViewMode.SCALED) return true
                initBookmarkItems(ViewMode.SCALED)
                appPreference?.setDoujinViewMode(ViewMode.SCALED)
            }

            R.id.action_view_mini_grid -> {
                if (itemAdapter.getViewMode() == ViewMode.MINI_GRID) return true
                initBookmarkItems(ViewMode.MINI_GRID)
                appPreference?.setDoujinViewMode(ViewMode.MINI_GRID)
            }
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appPreference = MyAppPreference.getInstance(requireContext())

        if (savedInstanceState != null) {
            // Restores action mode
            val shouldRestartActionMode = savedInstanceState.getBoolean(ACTION_MODE, false)
            if (shouldRestartActionMode) {
                startActionMode()
                actionMode?.title = viewModel.selectedBookmarkCount().toString() + " selected"
            }
        }

        initBookmarkGroups()
        initBookmarkItems(appPreference?.getDoujinViewMode() ?: ViewMode.SCALED)
        initUi()

        viewModel.groupList.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            viewModel.initializeGroups(it)
        })

        viewModel.bookmarkGroups().observe(viewLifecycleOwner, Observer {
            groupAdapter.submitList(it)
            btnContextMenu.visibility = View.VISIBLE

            // If all bookmarks have been removed by user
            if (it.isNullOrEmpty()) {
                header.text = "No bookmark exists"
                btnContextMenu.visibility = View.GONE
            } else {
                if (viewModel.selectedGroupName.isNotBlank()) {
                    header.text = viewModel.selectedGroupName
                }
            }
        })

        viewModel.selectedBookmarkGroup().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            viewModel.fetchBookmarkItems(it)
            header.text = it.name
        })

        viewModel.bookmarkItems.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            viewModel.selectedGroup?.let {
                viewModel.fetchBookmarkItems(it)
                viewModel.refreshGroupInfo()
            }
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

    private fun initBookmarkItems(viewMode: ViewMode) {
        itemAdapter.setViewMode(viewMode)

        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                when (viewMode) {
                    ViewMode.NORMAL_GRID -> 2
                    ViewMode.MINI_GRID -> 3
                    ViewMode.SCALED -> 2
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                when (viewMode) {
                    ViewMode.NORMAL_GRID -> 4
                    ViewMode.MINI_GRID -> 5
                    ViewMode.SCALED -> 4
                }
            }
            else -> 2
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)

        listItems.adapter = itemAdapter
        listItems.layoutManager = gridLayoutManager
        listItems.setHasFixedSize(true)
        listItems.itemAnimator = null

        // Prevent the same decor from stacking on top of each other.
        if (listItems.itemDecorationCount == 0) {
            val itemDecoration =
                GridItemDecoration(
                    2,
                    4,
                    includeEdge = true
                )
            listItems.addItemDecoration(itemDecoration)
        }

        viewModel.processedBookmarks().observe(viewLifecycleOwner, Observer {
            itemAdapter.submitList(it.toList())
        })
    }

    private fun initUi() {
        registerForContextMenu(btnContextMenu)
        btnContextMenu.setOnClickListener {
            it.showContextMenu()
        }

        fab.setOnClickListener {
            listItems.scrollToPosition(0) // Scrolls to top
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
        menu.add(MENU_DELETE_BOOKMARK_GROUP)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val selectedCollection = viewModel.selectedGroup ?: return true

        when (item.title) {
            MENU_CHANGE_NAME -> showChangeNameDialog(selectedCollection)
            MENU_DELETE_BOOKMARK_GROUP -> showDeleteGroupDialog(selectedCollection)
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
            setMessage("Are you sure you want to delete this bookmark group? Existing items will be lost")
            setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                viewModel.deleteGroup(group)
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->
            })
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteItemsDialog() {
        val dialog = DialogDeleteItems.newInstance()
        dialog.show(childFragmentManager, DIALOG_DELETE_ITEMS)
    }

    override fun deleteItems() {
        viewModel.deleteItems()
        actionMode?.finish()
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    override fun destroyActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    override fun startActionMode() {
        if (requireActivity() is AppCompatActivity) {
            val appCompat = requireActivity() as AppCompatActivity
//            actionMode = appCompat.startSupportActionMode(ActionModeCallback())

            actionModeCallback = ActionModeCallback()
            actionMode = appCompat.startSupportActionMode(actionModeCallback!!)

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
                R.id.action_delete -> showDeleteItemsDialog()
            }
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().let {
                it.menuInflater.inflate(R.menu.menu_doujin_bookmarks_contextual, menu)
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
            actionModeCallback = null
            requireActivity().window.statusBarColor = statusBarColor // Restores the original status bar color
            itemAdapter.actionModeEnabled = false
            viewModel.clearSelectedBookmarks()
        }
    }

    companion object {
        const val MENU_CHANGE_NAME = "Change Name"
        const val MENU_DELETE_BOOKMARK_GROUP = "Delete Bookmark Group"
        const val DIALOG_NEW_GROUP = "new_group_dialog"
        const val DIALOG_CHANGE_NAME = "change_name_dialog"
        const val DIALOG_DELETE_GROUP = "delete_group_dialog"
        const val DIALOG_DELETE_ITEMS = "delete_items_dialog"

        const val APPBAR_TITLE = "Bookmarks"

        @JvmStatic
        fun newInstance() =
            BookmarkFragment()
    }
}