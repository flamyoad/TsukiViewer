package com.flamyoad.tsukiviewer.ui.home.tags

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.BaseFragment

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.TagType
import com.flamyoad.tsukiviewer.utils.SimpleDialog
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_doujin_tags.*

class DoujinTagsFragment : BaseFragment(), SearchView.OnQueryTextListener {

    private val viewModel by activityViewModels<DoujinTagsViewModel>()

    private var searchView: SearchView? = null

    private val tagList = listOf(
        TagType.All,
        TagType.Parodies,
        TagType.Characters,
        TagType.Tags,
        TagType.Artists,
        TagType.Groups,
        TagType.Languages,
        TagType.Categories
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doujin_tags, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tags_fragment, menu)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> openSortDialog()
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewpager.adapter = TagFragmentAdapter(requireActivity(), tagList)

        TabLayoutMediator(tabLayout, viewpager, false, false) { tab, position ->
            tab.text = tagList[position].toString()
            viewpager.setCurrentItem(tab.position, false)
        }.attach()

        // Disables swiping of viewpager
        viewpager.isUserInputEnabled = false

        viewModel.searchTerms().observe(viewLifecycleOwner, Observer {  })
    }

    private fun openSortDialog() {
        val dialog = TagSortingDialog()
        dialog.show(childFragmentManager, "sortDialog")
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DoujinTagsFragment()

        const val APPBAR_TITLE = "Tags"
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.setQuery(newText ?: "")
        return true
    }
}
