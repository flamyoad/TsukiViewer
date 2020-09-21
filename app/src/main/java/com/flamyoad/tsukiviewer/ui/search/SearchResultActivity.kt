package com.flamyoad.tsukiviewer.ui.search

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.coroutines.launch

class SearchResultActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var viewmodel: SearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        viewmodel = ViewModelProvider(this).get(SearchResultViewModel::class.java)

        initToolbar()

        val title = intent.getStringExtra(SearchActivity.SEARCH_TITLE) ?: ""
        val tags = intent.getStringExtra(SearchActivity.SEARCH_TAGS) ?: ""

        lifecycleScope.launch {
            viewmodel.submitQuery(title, tags)
        }

        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_result, menu)
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

        viewmodel.isLoading().observe(this, Observer { isLoading ->
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

        val adapter = LocalDoujinsAdapter()

        // StateRestorationPolicy is in alpha stage. It may crash the app
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        listSearchedDoujins.adapter = adapter
        listSearchedDoujins.layoutManager = gridLayoutManager

        val itemDecoration = GridItemDecoration(spanCount, 4, includeEdge = true)

        listSearchedDoujins.addItemDecoration(itemDecoration)

        viewmodel.searchedResult().observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewmodel.filterList(newText ?: "")
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }
}
