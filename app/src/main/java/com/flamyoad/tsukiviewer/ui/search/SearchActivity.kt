package com.flamyoad.tsukiviewer.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.SearchHistoryAdapter
import com.flamyoad.tsukiviewer.databinding.ActivitySearchBinding
import com.flamyoad.tsukiviewer.model.SearchHistory
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.utils.ActivityStackUtils
import com.google.android.material.chip.Chip

class SearchActivity : AppCompatActivity(), TagSelectedListener {
    
    private lateinit var binding: ActivitySearchBinding
    
    private val viewModel: SearchViewModel by viewModels()

    companion object {
        const val SEARCH_TITLE = "SearchActivity.SEARCH_TITLE"
        const val SEARCH_TAGS = "SearchActivity.SEARCH_TAGS"
        const val SEARCH_INCLUDE_ALL_TAGS = "SearchActivity.SEARCH_INCLUDE_ALL_TAGS"

        private const val DIALOG_FRAGMENT_TAG = "fragment_tag_list"
        private const val SELECTED_TAGS = "SearchActivity.SELECTED_TAGS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            val tags = it.getStringArray(SELECTED_TAGS)
            if (tags != null) {
                restoreTagList(tags)
            }
        }

        initSearchView()
        initSearchHistory()

        binding.btnSearch.setOnClickListener {
            submitSearch()
        }

        binding.chipAddItem.setOnClickListener {
            val tagListFragment = TagPickerDialogFragment.newInstance()
            tagListFragment.show(supportFragmentManager, DIALOG_FRAGMENT_TAG)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val selectedTags = binding.chipGroup.children
            .map { x -> (x as Chip).text.toString() }
            .toList()
            .toTypedArray()

        outState.putStringArray(SELECTED_TAGS, selectedTags)
    }

    private fun restoreTagList(tagNames: Array<String>) {
        for (tag in tagNames) {
            this.addChip(tag)
        }
    }

    private fun addChip(tagName: String) {
        val chips = binding.chipGroup.children as Sequence<Chip>

        // Check for duplicates. If yes, then return
        for (chip in chips) {
            if (tagName == chip.text) {
                return
            }
        }

        val chip = layoutInflater.inflate(R.layout.tag_list_chip, binding.chipGroup, false) as Chip
        chip.text = tagName

        binding.chipGroup.addView(chip)
        revalidateCheckBox()

        chip.setOnCloseIconClickListener {
            binding.chipGroup.removeView(it)
            revalidateCheckBox()
        }
    }

    override fun onTagSelected(tag: Tag) {
        addChip(tag.name)

        // Dismisses the dialog once an item is clicked
        val dialog = supportFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) as DialogFragment
        dialog.dismiss()

        viewModel.clearQuery()
    }

    // Hides the checkbox if the user did not choose any tags. Otherwise, show the checkbox
    private fun revalidateCheckBox() {
        if (binding.chipGroup.childCount > 1)
            binding.checkbox.visibility = View.VISIBLE
        else
            binding.checkbox.visibility = View.GONE
    }

    private fun submitSearch() {
        // Start from 1 because the first child is the "+" button
        val selectedTags = mutableListOf<String>()
        for (i in 1 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            selectedTags.add(chip.text.toString())
        }

        val title: String = binding.searchBarInclude.searchView.query.toString()
        val tags: String = selectedTags.joinToString(",")

        if (title.isBlank() && tags.isBlank()) {
            return
        }

        val includeAllTags = binding.checkbox.isChecked

        val searchHistory = SearchHistory(
            title = title,
            tags = tags,
            mustIncludeAllTags = includeAllTags
        )
        viewModel.insertSearchHistory(searchHistory)

        val intent = Intent(this, SearchResultActivity::class.java).apply {
            putExtra(SEARCH_TITLE, title)
            putExtra(SEARCH_TAGS, tags)
            putExtra(SEARCH_INCLUDE_ALL_TAGS, includeAllTags)
        }

        startActivity(intent)
    }

    private fun initSearchView() {
        binding.searchBarInclude.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        val adapter = SearchHistoryAdapter(
            this::onHistoryItemClick,
            this::deleteHistory
        )

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            /* RecyclerView does not scroll to top automatically if new item is present.
                So, we have to scroll to top if the first item in old list is VISIBLE.

                Otherwise, it will look like the new item is not visible because the list still shows items from
                2th ~ nth position
            */
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart != 0) {
                    return
                }

                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                // Only scroll if first or second item in the new list is visible
                if (firstVisible <= 1) {
                    layoutManager.scrollToPosition(0)
                }
            }
        })

        val itemDeco = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)

        binding.listSearchHistory.adapter = adapter
        binding.listSearchHistory.layoutManager = layoutManager
        binding.listSearchHistory.addItemDecoration(itemDeco)

        viewModel.searchHistories.observe(this, Observer {
            adapter.submitList(it)
        })
    }

    private fun onHistoryItemClick(item: SearchHistory) {
        val intent = Intent(this, SearchResultActivity::class.java).apply {
            putExtra(SEARCH_TITLE, item.title)
            putExtra(SEARCH_TAGS, item.tags)
            putExtra(SEARCH_INCLUDE_ALL_TAGS, item.mustIncludeAllTags)
        }

        startActivity(intent)
    }

    private fun deleteHistory(item: SearchHistory) {
        viewModel.deleteSearchHistory(item)
    }
}
