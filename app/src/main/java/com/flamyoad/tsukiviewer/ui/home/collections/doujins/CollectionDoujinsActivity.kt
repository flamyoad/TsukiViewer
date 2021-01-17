package com.flamyoad.tsukiviewer.ui.home.collections.doujins

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.ViewMode
import com.flamyoad.tsukiviewer.ui.editor.EditorActivity
import com.flamyoad.tsukiviewer.ui.home.collections.CollectionFragment
import com.flamyoad.tsukiviewer.ui.home.collections.DialogCollectionInfo
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_collection_doujins.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val ACTION_MODE = "action_mode"
private const val ADD_BOOKMARK_DIALOG = "add_bookmark_dialog"
private const val SEARCH_VIEW = "search_view"

class CollectionDoujinsActivity : AppCompatActivity(),
    ActionModeListener<Doujin>,
    SearchView.OnQueryTextListener {

    private val viewModel: CollectionDoujinsViewModel by viewModels()

    private val adapter = LocalDoujinsAdapter(this).apply {
        setHasStableIds(true)
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private var appPreference = MyAppPreference.getInstance(this)

    private var searchView: SearchView? = null
    private var actionMode: ActionMode? = null
    private var statusBarColor: Int = -1
    private var previousSearchQuery: String = ""

    private var queryJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_doujins)
        initToolbar()

        initRecyclerView(appPreference.getDoujinViewMode())

        viewModel.snackbarText.observe(this, Observer { text ->
            if (text.isBlank()) return@Observer

            Snackbar.make(parentLayout, text, Snackbar.LENGTH_LONG).show()
            viewModel.snackbarText.value = ""
        })

        viewModel.selectionCountText().observe(this, Observer {
            if (actionMode != null) {
                actionMode?.title = it.toString() + " selected"
                actionMode?.invalidate()
            }
        })

        val collectionId = intent.getLongExtra(CollectionFragment.COLLECTION_ID, -1)
        viewModel.submitQuery(collectionId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_collection_doujins, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        if (previousSearchQuery.isNotBlank()) {
            searchItem.expandActionView()
            searchView?.setQuery(previousSearchQuery, false)
            searchView?.clearFocus()
        }

        return true
    }

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

            R.id.action_more_info -> {
                val collectionId = intent.getLongExtra(CollectionFragment.COLLECTION_ID, -1)
                val dialog = DialogCollectionInfo.newInstance(collectionId)
                dialog.show(supportFragmentManager, DialogCollectionInfo.NAME)
            }

            R.id.action_view_normal_grid -> {
                if (adapter.getViewMode() == ViewMode.NORMAL_GRID) return true
                initRecyclerView(ViewMode.NORMAL_GRID)
                appPreference.setDoujinViewMode(ViewMode.NORMAL_GRID)
            }

            R.id.action_view_scaled -> {
                if (adapter.getViewMode() == ViewMode.SCALED) return true
                initRecyclerView(ViewMode.SCALED)
                appPreference.setDoujinViewMode(ViewMode.SCALED)
            }

            R.id.action_view_mini_grid -> {
                if (adapter.getViewMode() == ViewMode.MINI_GRID) return true
                initRecyclerView(ViewMode.MINI_GRID)
                appPreference.setDoujinViewMode(ViewMode.MINI_GRID)
            }
        }
        return true
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

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collectionName = intent.getStringExtra(CollectionFragment.COLLECTION_NAME)
        val criterias = intent.getStringExtra(CollectionFragment.COLLECTION_CRITERIAS)

        txtCollectionName.text = collectionName
        txtCriterias.text = criterias
    }

    private fun initRecyclerView(viewMode: ViewMode) {
        adapter.setViewMode(viewMode)

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

        val gridLayoutManager = GridLayoutManager(this, spanCount)

        listDoujins.adapter = adapter
        listDoujins.layoutManager = gridLayoutManager
        listDoujins.setHasFixedSize(true)
        listDoujins.itemAnimator = null

        val itemDecoration = GridItemDecoration(spanCount, 4, includeEdge = true)

        listDoujins.addItemDecoration(itemDecoration)

        viewModel.searchedResult().observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterList(newText ?: "")
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
    }

    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_bookmark -> {
                    viewModel.fetchBookmarkGroup()
                    val dialog = BookmarkGroupDialog.newInstance()
                    dialog.show(supportFragmentManager, ADD_BOOKMARK_DIALOG)
                }
                R.id.action_edit -> {
                    openEditor()
                }

                R.id.action_select_all -> {
                    viewModel.tickSelectedDoujinsAll()
                }
            }
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_search_result_contextual, menu)
            statusBarColor = window.statusBarColor // Stores the original status bar color

            val colorPrimaryLight =
                ContextCompat.getColor(this@CollectionDoujinsActivity, R.color.colorPrimaryLight)
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

        private fun openEditor() {
            val jobIsActive = queryJob?.isActive ?: false
            if (jobIsActive) {
                return
            }

            if (viewModel.selectedCount() == 1) {
                val dirPath = viewModel.getSelectedDoujins().first().path.absolutePath
                val doujinTitle = viewModel.getSelectedDoujins().first().title

                val newIntent = Intent(this@CollectionDoujinsActivity, EditorActivity::class.java).apply {
                    putExtra(EditorActivity.HAS_MULTIPLE_ITEMS, false)
                    putExtra(EditorActivity.DOUJIN_FILE_PATH, dirPath)
                    putExtra(EditorActivity.DOUJIN_NAME, doujinTitle)
                }
                this@CollectionDoujinsActivity.startActivity(newIntent)

            } else {
                queryJob = lifecycleScope.launch(Dispatchers.Default) {
                    val dirPaths = viewModel.getSelectedDoujins()
                        .map { doujin -> doujin.path.absolutePath }
                        .toTypedArray()

                    val intent = Intent(this@CollectionDoujinsActivity, EditorActivity::class.java)
                    intent.apply {
                        putExtra(EditorActivity.HAS_MULTIPLE_ITEMS, true)
                        putExtra(EditorActivity.DOUJIN_MULTIPLE_FILE_PATHS, dirPaths)
                        putExtra(EditorActivity.DOUJIN_NAME, "Batch Editing")
                    }

                    this@CollectionDoujinsActivity.startActivity(intent)
                }
            }
        }
    }
}
