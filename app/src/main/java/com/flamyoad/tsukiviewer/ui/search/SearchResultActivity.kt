package com.flamyoad.tsukiviewer.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.R
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity() {

    private lateinit var viewmodel: SearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        viewmodel = ViewModelProvider(this).get(SearchResultViewModel::class.java)

        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = getIntent()

        txtSearchTitle.text = intent.getStringExtra(SearchActivity.SEARCH_TITLE)
        txtSearchTags.text = "sole female, netorate"
    }
}
