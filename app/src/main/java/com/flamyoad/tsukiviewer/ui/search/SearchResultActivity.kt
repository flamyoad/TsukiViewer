package com.flamyoad.tsukiviewer.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // query is null here, but not in previous activity? why
        val query = intent.getStringExtra(SearchActivity.SEARCH_TITLE)

        lifecycleScope.launch {
            viewmodel.submitQuery(query)
        }

        initRecyclerView()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(intent) {
            txtSearchTitle.text = getStringExtra(SearchActivity.SEARCH_TITLE)
            txtSearchTags.text = "sole female, netorate"
        }
    }

    private fun initRecyclerView() {
        val adapter = LocalDoujinsAdapter()
        val gridLayoutManager = GridLayoutManager(this, 2)

        listSearchedDoujins.adapter = adapter
        listSearchedDoujins.layoutManager = gridLayoutManager
        listSearchedDoujins.addItemDecoration(ItemOffsetDecoration(8))

        viewmodel.searchResult.observe(this, Observer {
            adapter.setList(it)
        })
    }
}
