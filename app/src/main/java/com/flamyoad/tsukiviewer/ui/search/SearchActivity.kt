package com.flamyoad.tsukiviewer.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.SearchHistoryAdapter
import com.flamyoad.tsukiviewer.model.SearchHistory
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_bar.*

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_TITLE = "search_title"
        const val SEARCH_TAGS = "search_tags"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initSearchView()
        initSearchHistory()

        btnSearch.setOnClickListener {
            val query = searchView.query
            if (query != null && query.isNotBlank()) {
                val intent = Intent(this@SearchActivity , SearchResultActivity::class.java)
                intent.putExtra(SEARCH_TITLE, query)
                startActivity(intent)
            }
        }
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotBlank()) {
                    val intent = Intent(this@SearchActivity , SearchResultActivity::class.java)
                    intent.putExtra(SEARCH_TITLE, query)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun initSearchHistory() {
        val adapter = SearchHistoryAdapter()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val itemDeco = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)

        listSearchHistory.adapter = adapter
        listSearchHistory.layoutManager = linearLayoutManager
        listSearchHistory.addItemDecoration(itemDeco)

//        val mockList = listOf<SearchHistory>(
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang"),
//            SearchHistory(title = "Hikawa Sayo", tags = "bang")
//            )

//        adapter.setList(mockList)
    }
}
