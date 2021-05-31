package com.flamyoad.tsukiviewer.ui.home.local

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.Source
import com.flamyoad.tsukiviewer.model.ViewMode
import com.flamyoad.tsukiviewer.ui.editor.EditorActivity
import com.flamyoad.tsukiviewer.ui.home.collections.doujins.CollectionDoujinsActivity
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.utils.ui.GridItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_local_doujins.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

private const val ACTION_MODE = "action_mode"
private const val ADD_BOOKMARK_DIALOG = "add_bookmark_dialog"

// onResume() is called after onActivityCreated() in Fragment
class LocalDoujinsFragment : BaseFragment(),
    ActionModeListener<Doujin>,
    LocalDoujinsContextualListener,
    SelectSourceListener {

    private val viewModel: LocalDoujinViewModel by activityViewModels()

    private var appPreference: MyAppPreference? = null

    private var adapter = LocalDoujinsAdapter(this)
        .apply { setHasStableIds(true) }

    private var actionMode: ActionMode? = null
    private var statusBarColor: Int = -1

    private var queryJob: Job? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var toast: Toast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_doujins, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true) 
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isInActionMode = actionMode != null
        outState.putBoolean(ACTION_MODE, isInActionMode)
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadDoujins()
    }

    // onPrepareOptionsMenu() is called after onActivityCreated()
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val progressMenuItem = menu.findItem(R.id.progress_bar_sync)
        val progressActionView = progressMenuItem.actionView
        progressBar = progressActionView.findViewById(R.id.progressBarSync)
        progressBar.visibility = View.GONE

        val sortMenuItem = menu.findItem(R.id.action_sort_dialog)

        viewModel.isLoading().observe(viewLifecycleOwner, Observer { stillLoading ->
            if (stillLoading) {
                progressBar.visibility = View.VISIBLE
                sortMenuItem.isVisible = false
            } else {
                progressBar.visibility = View.GONE
                sortMenuItem.isVisible = true
                listLocalDoujins.visibility = View.VISIBLE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_local_doujins, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search_local -> {
                openSearchActivity()
            }

            // https://stackoverflow.com/questions/47045788/fragment-declared-target-fragment-that-does-not-belong-to-this-fragmentmanager
            R.id.action_sync -> {
                val dialog = DialogSelectSource.newInstance()
                dialog.setTargetFragment(
                    this,
                    0
                ) // For passing methods from this fragment to dialog
                dialog.show(requireActivity().supportFragmentManager, DialogSelectSource.name)
            }

            R.id.action_sort_dialog -> {
                openSortDialog()
            }

            R.id.action_view_normal_grid -> {
                if (adapter.getViewMode() == ViewMode.NORMAL_GRID) return true
                initRecyclerView(ViewMode.NORMAL_GRID)
                appPreference?.setDoujinViewMode(ViewMode.NORMAL_GRID)
            }

            R.id.action_view_scaled -> {
                if (adapter.getViewMode() == ViewMode.SCALED) return true
                initRecyclerView(ViewMode.SCALED)
                appPreference?.setDoujinViewMode(ViewMode.SCALED)
            }

            R.id.action_view_mini_grid -> {
                if (adapter.getViewMode() == ViewMode.MINI_GRID) return true
                initRecyclerView(ViewMode.MINI_GRID)
                appPreference?.setDoujinViewMode(ViewMode.MINI_GRID)
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appPreference = MyAppPreference.getInstance(requireContext())
        initRecyclerView(appPreference?.getDoujinViewMode() ?: ViewMode.SCALED)

        if (savedInstanceState != null) {
            val shouldRestartActionMode = savedInstanceState.getBoolean(ACTION_MODE, false)
            if (shouldRestartActionMode) {
                startActionMode()
                actionMode?.title = viewModel.selectedCount().toString() + " selected"
            }
        }

        toast = Toast.makeText(context, "", Toast.LENGTH_LONG)

        viewModel.toastText().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                toast.setText(it)
                toast.show()
            }
        })

        viewModel.snackbarText.observe(viewLifecycleOwner, Observer { text ->
            if (text.isNullOrBlank()) return@Observer

            Snackbar.make(rootView, text, Snackbar.LENGTH_LONG)
                .show()

            viewModel.snackbarText.value = ""
        })

        viewModel.isSorting().observe(viewLifecycleOwner, Observer { stillSorting ->
            if (stillSorting) {
                listLocalDoujins.alpha = 0.6f
                fastScroller.isEnabled = false
                sortingIndicator.visibility = View.VISIBLE
            } else {
                listLocalDoujins.alpha = 1f
                fastScroller.isEnabled = true
                sortingIndicator.visibility = View.GONE
            }
        })
    }

    private fun initRecyclerView(viewMode: ViewMode) {
        adapter.setViewMode(viewMode)
        adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                when (viewMode) {
                    ViewMode.NORMAL_GRID -> 2
                    ViewMode.MINI_GRID -> 3
                    ViewMode.SCALED -> 2
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                when (viewMode) {
                    ViewMode.NORMAL_GRID -> 4
                    ViewMode.MINI_GRID -> 5
                    ViewMode.SCALED -> 4
                }
            }
            else -> 2
        }

        val gridLayoutManager = GridLayoutManager(context, spanCount)

        listLocalDoujins.swapAdapter(adapter, false)
        listLocalDoujins.layoutManager = gridLayoutManager

        // Prevent the same decor from stacking on top of each other.
        if (listLocalDoujins.itemDecorationCount == 0) {
            val itemDecoration =
                GridItemDecoration(
                    spanCount,
                    10,
                    includeEdge = true
                )
            listLocalDoujins.addItemDecoration(itemDecoration)
        }

        listLocalDoujins.setHasFixedSize(true)
        listLocalDoujins.itemAnimator = null

        viewModel.doujinList().observe(viewLifecycleOwner, Observer { newList ->
            adapter.setList(newList)
            if (newList.isNotEmpty()) {
                listLocalDoujins.visibility = View.VISIBLE
            }
        })
    }

    private fun openSortDialog() {
        val dialog = DialogSortDoujin()
        dialog.show(childFragmentManager, "sortdialog")
    }

    private fun openSearchActivity() {
        val intent = Intent(context, SearchActivity::class.java)
        startActivity(intent)
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    override fun destroyActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    companion object {
        @JvmStatic
        fun newInstance(): LocalDoujinsFragment {
            return LocalDoujinsFragment()
        }

        const val APPBAR_TITLE = "Local Storage"
    }

    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_bookmark -> {
                    viewModel.fetchBookmarkGroup()
                    val dialog = DialogBookmarkItems.newInstance()
                    dialog.show(childFragmentManager, ADD_BOOKMARK_DIALOG)
                }
                R.id.action_edit -> {
                    openEditor()
                }
            }

            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().let {
                it.menuInflater.inflate(R.menu.menu_local_doujins_contextual, menu)
                statusBarColor = it.window.statusBarColor // Stores the original status bar color

                val colorPrimaryLight = ContextCompat.getColor(it, R.color.colorPrimaryLight)
                it.window.statusBarColor =
                    colorPrimaryLight // Changes status bar color in action mode
            }

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            requireActivity().window.statusBarColor =
                statusBarColor // Restores the original status bar color

            adapter.actionModeEnabled = false
            viewModel.clearSelectedDoujins()
        }

        private fun openEditor() {
            val jobIsActive = queryJob?.isActive ?: false
            if (jobIsActive) {
                return
            }

            val context = this@LocalDoujinsFragment.requireContext()

            if (viewModel.selectedCount() == 1) {
                val dirPath = viewModel.getSelectedDoujins().first().path.absolutePath
                val doujinTitle = viewModel.getSelectedDoujins().first().title

                val newIntent =
                    Intent(context, EditorActivity::class.java).apply {
                        putExtra(EditorActivity.HAS_MULTIPLE_ITEMS, false)
                        putExtra(EditorActivity.DOUJIN_FILE_PATH, dirPath)
                        putExtra(EditorActivity.DOUJIN_NAME, doujinTitle)
                    }
                context.startActivity(newIntent)

            } else {
                queryJob = lifecycleScope.launch(Dispatchers.Default) {
                    val dirPaths = viewModel.getSelectedDoujins()
                        .map { doujin -> doujin.path.absolutePath }
                        .toTypedArray()

                    val intent = Intent(context, EditorActivity::class.java)
                    intent.apply {
                        putExtra(EditorActivity.HAS_MULTIPLE_ITEMS, true)
                        putExtra(EditorActivity.DOUJIN_MULTIPLE_FILE_PATHS, dirPaths)
                        putExtra(EditorActivity.DOUJIN_NAME, "Batch Editing")
                    }

                    context.startActivity(intent)
                }
            }
        }
    }

    override fun startActionMode() {
        if (requireActivity() is AppCompatActivity) {
            val appCompat = requireActivity() as AppCompatActivity
            actionMode = appCompat.startSupportActionMode(ActionModeCallback())
            adapter.actionModeEnabled = true
        }
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

    override fun cancelActionMode() {
        actionMode?.finish()
        viewModel.clearSelectedDoujins()
    }

    override fun onFetchMetadata(sources: EnumSet<Source>) {
        viewModel.fetchMetadata(sources)
    }

}