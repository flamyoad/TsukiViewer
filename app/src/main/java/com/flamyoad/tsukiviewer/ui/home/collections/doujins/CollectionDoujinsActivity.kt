package com.flamyoad.tsukiviewer.ui.home.collections.doujins

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.ui.home.collections.CollectionFragment
import com.flamyoad.tsukiviewer.ui.home.collections.DialogCollectionInfo
import com.flamyoad.tsukiviewer.ui.search.BookmarkGroupDialog
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_collection_doujins.*

private const val ACTION_MODE = "action_mode"
private const val ADD_BOOKMARK_DIALOG = "add_bookmark_dialog"
private const val SEARCH_VIEW = "search_view"

class CollectionDoujinsActivity : AppCompatActivity(),
    ActionModeListener<Doujin>,
    SearchView.OnQueryTextListener {

    private val viewModel: CollectionDoujinsViewModel by viewModels()

    private lateinit var adapter: LocalDoujinsAdapter

    private var searchView: SearchView? = null
    private var actionMode: ActionMode? = null
    private var statusBarColor: Int = -1
    private var previousSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_doujins)
        initToolbar()
        initRecyclerView()

        viewModel.snackbarText.observe(this, Observer { text ->
            if (text.isBlank()) return@Observer

            Snackbar.make(parentLayout, text, Snackbar.LENGTH_LONG).show()
            viewModel.snackbarText.value = ""
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
        }
        return true
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
    }
}
