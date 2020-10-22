package com.flamyoad.tsukiviewer.ui.search

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_search_result.*

private const val ACTION_MODE = "action_mode"
private const val ADD_BOOKMARK_DIALOG = "add_bookmark_dialog"
private const val SEARCH_VIEW = "search_view"

class SearchResultActivity : AppCompatActivity(),
    ActionModeListener<Doujin>,
    SearchView.OnQueryTextListener {

    private val viewModel: SearchResultViewModel by viewModels()

    private lateinit var adapter: LocalDoujinsAdapter

    private var searchView: SearchView? = null
    private var actionMode: ActionMode? = null
    private var statusBarColor: Int = -1
    private var previousSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        initRecyclerView()

        initToolbar()

        val title = intent.getStringExtra(SearchActivity.SEARCH_TITLE) ?: ""
        val tags = intent.getStringExtra(SearchActivity.SEARCH_TAGS) ?: ""
        val includeAllTags = intent.getBooleanExtra(SearchActivity.SEARCH_INCLUDE_ALL_TAGS, false)

        viewModel.submitQuery(title, tags, includeAllTags)

        viewModel.snackbarText.observe(this, Observer { text ->
            if (text.isBlank()) return@Observer

            Snackbar.make(parentLayout, text, Snackbar.LENGTH_LONG)
                .show()

            viewModel.snackbarText.value = ""
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isInActionMode = actionMode != null

        outState.putBoolean(ACTION_MODE, isInActionMode)
        outState.putString(SEARCH_VIEW, searchView?.query.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val shouldRestartActionMode = savedInstanceState.getBoolean(ACTION_MODE, false)
        if (shouldRestartActionMode) {
            startActionMode()
            actionMode?.title = viewModel.selectedCount().toString() + " selected"
        }
        previousSearchQuery = savedInstanceState.getString(SEARCH_VIEW) ?: ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_result, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        if (previousSearchQuery.isNotBlank()) {
            searchItem.expandActionView()
            searchView?.setQuery(previousSearchQuery, false)
            searchView?.clearFocus()
        }

        return true
    }

    /*
        In this method, the SearchView is disabled before all the items have finished loading.
        This is because it will be very error prone to incorporate searching for second time
        while the original search result is still being processed.

        I try to avoid it because searching is done in multithreaded manner using Coroutines and
        the List which contains the searched doujins is a mutable shared state.

        Only after the original search result has done loading, the SearchView is set as visible
        to allow user to perform searching for the 2nd time.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val progressBarItem = menu.findItem(R.id.progress_bar_loading)
        val progressActionView = progressBarItem.actionView
        val progressBar: ProgressBar = progressActionView.findViewById(R.id.progressBarSync)

        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        searchItem?.isVisible = false

        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        viewModel.isLoading().observe(this, Observer { isLoading ->
            if (!isLoading) { // If has done loading all items ...
                progressBar.visibility = View.GONE
                searchItem.isVisible = true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(intent) {
            val title = intent.getStringExtra(SearchActivity.SEARCH_TITLE)
            val tags = intent.getStringExtra(SearchActivity.SEARCH_TAGS)

            if (title.isNullOrBlank()) {
                txtSearchTitle.text = "title: {}"
            } else {
                txtSearchTitle.text = getStringExtra(SearchActivity.SEARCH_TITLE)
            }

            if (tags.isNullOrBlank()) {
                txtSearchTags.text = "tags: {}"
            } else {
                txtSearchTags.text = getStringExtra(SearchActivity.SEARCH_TAGS)
            }

        }
    }

    private fun initRecyclerView() {
        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }

        val gridLayoutManager = GridLayoutManager(this, spanCount)

        adapter = LocalDoujinsAdapter(this)

        adapter.apply {
            setHasStableIds(true)
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        listSearchedDoujins.adapter = adapter
        listSearchedDoujins.layoutManager = gridLayoutManager
        listSearchedDoujins.setHasFixedSize(true)
        listSearchedDoujins.itemAnimator = null

        val itemDecoration = GridItemDecoration(spanCount, 4, includeEdge = true)

        listSearchedDoujins.addItemDecoration(itemDecoration)

        viewModel.searchedResult().observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterList(newText ?: "")
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun startActionMode() {
        actionMode = startSupportActionMode(ActionModeCallback())
        adapter.actionModeEnabled = true
    }

    override fun onMultiSelectionClick(item: Doujin) {
        viewModel.tickSelectedDoujin(item)

        val count = viewModel.selectedCount()
        if (count == 0) {
            actionMode?.finish()
            viewModel.clearSelectedDoujins()
        }

        actionMode?.title = count.toString() + " selected"
        actionMode?.invalidate()
    }

    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_bookmark -> {
                    val dialog = BookmarkGroupDialog.newInstance()
                    dialog.show(supportFragmentManager, ADD_BOOKMARK_DIALOG)
                }
                R.id.action_edit -> {
                }

                R.id.action_select_all -> {

                }
            }
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_search_result_contextual, menu)
            statusBarColor = window.statusBarColor // Stores the original status bar color

            val colorPrimaryLight =
                ContextCompat.getColor(this@SearchResultActivity, R.color.colorPrimaryLight)
            window.statusBarColor =
                colorPrimaryLight // Changes status bar color in action mode
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            window.statusBarColor = statusBarColor // Restores the original status bar color

            adapter.actionModeEnabled = false
            viewModel.clearSelectedDoujins()
        }

    }
}
