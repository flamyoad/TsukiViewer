package com.flamyoad.tsukiviewer.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.SearchHistoryAdapter
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_bar.*

class SearchActivity : AppCompatActivity(), TagSelectedListener {

    private lateinit var viewModel: SearchViewModel

    companion object {
        const val SEARCH_TITLE = "SearchActivity.SEARCH_TITLE"
        const val SEARCH_TAGS = "SearchActivity.SEARCH_TAGS"
        const val DIALOG_FRAGMENT_TAG = "fragment_tag_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        initSearchView()
        initSearchHistory()

        btnSearch.setOnClickListener {
            submitSearch()
        }

        chipAddItem.setOnClickListener {
            val tagListFragment = TagPickerDialogFragment.newInstance()
            tagListFragment.show(supportFragmentManager, DIALOG_FRAGMENT_TAG)
        }
    }

    override fun onTagSelected(tagName: String) {
        addChip(tagName)

        val dialog = supportFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) as DialogFragment
        dialog.dismiss()

        viewModel.clearQuery()
    }

    private fun addChip(text: String) {
        val chip = layoutInflater.inflate(R.layout.tag_list_chip, chipGroup, false) as Chip
        chip.text = text

        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
        }

        chipGroup.addView(chip)
    }

    private fun submitSearch() {
        // Start from 1 because the first child is the "+" button
        val selectedTags = mutableListOf<String>()
        for (i in 1 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            selectedTags.add(chip.text.toString())
        }

        val title = searchView.query.toString()
        val tags = selectedTags.joinToString(",")

        if (title.isBlank() && tags.isBlank()) {
            return
        }

        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra(SEARCH_TITLE, title)
        intent.putExtra(SEARCH_TAGS, tags)
        startActivity(intent)
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotBlank()) {
                    val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)

                    // No idea why but when you pass CharSequence in, the data is null in next activity,
                    // so I have to convert it to String before putting it in intent
                    intent.putExtra(SEARCH_TITLE, query.toString())
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
