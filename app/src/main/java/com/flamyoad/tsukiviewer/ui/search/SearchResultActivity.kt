package com.flamyoad.tsukiviewer.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.utils.ItemOffsetDecoration
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.coroutines.launch

class SearchResultActivity : AppCompatActivity() {

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

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu.findItem(R.id.progress_bar_loading)
        val view = menuItem.actionView

        val progressBar: ProgressBar = view.findViewById(R.id.progressBarSync)
        progressBar.visibility = View.GONE

        viewmodel.isLoading().observe(this, Observer { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

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
        val adapter = LocalDoujinsAdapter()
        val gridLayoutManager = GridLayoutManager(this, 2)

        listSearchedDoujins.adapter = adapter
        listSearchedDoujins.layoutManager = gridLayoutManager
        listSearchedDoujins.addItemDecoration(ItemOffsetDecoration(8))

        viewmodel.searchedResult().observe(this, Observer {
            adapter.setList(it)
        })
    }
}
