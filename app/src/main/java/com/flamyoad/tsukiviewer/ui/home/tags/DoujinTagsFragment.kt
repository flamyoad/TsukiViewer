package com.flamyoad.tsukiviewer.ui.home.tags

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.MyApplication

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.databinding.FragmentDoujinTagsBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.core.model.TagType
import com.flamyoad.tsukiviewer.utils.extensions.reduceDragSensitivity
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

private const val SEARCH_VIEW = "search_view"

class DoujinTagsFragment : BaseFragment(), SearchView.OnQueryTextListener {
    
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: DoujinTagsViewModel by activityViewModels { viewModelFactory }

    private var _binding: FragmentDoujinTagsBinding? = null
    private val binding get() = _binding!!

    private var searchView: SearchView? = null

    private var previousSearchQuery: String = ""

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        previousSearchQuery = savedInstanceState?.getString(SEARCH_VIEW) ?: ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_VIEW, searchView?.query.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoujinTagsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tags_fragment, menu)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        searchView?.setOnQueryTextListener(this)

        if (previousSearchQuery.isNotBlank()) {
            searchItem.expandActionView()
            searchView?.setQuery(previousSearchQuery, false)
            searchView?.clearFocus()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> openSortDialog()
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewpager.adapter = TagFragmentAdapter(requireActivity(), tagList)

        TabLayoutMediator(binding.tabLayout, binding.viewpager, false, true) { tab, position ->
            tab.text = tagList[position].toString()
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()
        
        binding.viewpager.reduceDragSensitivity()

        viewModel.searchTerms().observe(viewLifecycleOwner, Observer {  })
    }

    private fun openSortDialog() {
        val dialog = TagSortingDialog()
        dialog.show(childFragmentManager, "sortDialog")
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    override fun destroyActionMode() {}

    companion object {
        const val APPBAR_TITLE = "Tags"

        @JvmStatic
        fun newInstance() =
            DoujinTagsFragment()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.setQuery(newText ?: "")
        return true
    }
}
