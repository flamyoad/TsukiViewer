package com.flamyoad.tsukiviewer.ui.home.local

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import com.flamyoad.tsukiviewer.utils.snackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_local_doujins.*

private const val ACTION_MODE = "action_mode"
private const val ADD_BOOKMARK_DIALOG = "add_bookmark_dialog"

// onResume() is called after onActivityCreated() in Fragment
class LocalDoujinsFragment : BaseFragment(), ActionModeListener<Doujin> {
    private val viewModel: LocalDoujinViewModel by activityViewModels()

    private var adapter = LocalDoujinsAdapter(this)
    private var actionMode: ActionMode? = null
    private var statusBarColor: Int = -1

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

            R.id.action_sync -> {
                if (shouldShowSyncDialog()) {
                    openSyncAlertDialog()
                } else {
                    viewModel.fetchMetadataAll()
                }
            }

            R.id.action_refresh -> {

            }

            R.id.action_sort_dialog -> {
                openSortDialog()
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()

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

        viewModel.refreshResult().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                snackbar(it)
            }
        })
    }

    private fun initRecyclerView() {
        adapter = LocalDoujinsAdapter(this)
        adapter.setHasStableIds(true)

        // StateRestorationPolicy is in alpha stage
        adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }

        val gridLayoutManager = GridLayoutManager(context, spanCount)

        listLocalDoujins.swapAdapter(adapter, false)
        listLocalDoujins.layoutManager = gridLayoutManager

        val itemDecoration = GridItemDecoration(spanCount, 10, includeEdge = true)

        listLocalDoujins.addItemDecoration(itemDecoration)

        listLocalDoujins.setHasFixedSize(true)

        listLocalDoujins.itemAnimator = null

        viewModel.doujinList().observe(viewLifecycleOwner, Observer { newList ->
            adapter.setList(newList)
        })
    }

    private fun openSyncAlertDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_explanation_for_sync, null)

        val checkbox = view.findViewById<CheckBox>(R.id.checkBox)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Sync local comics with online metadata?")
            .setMessage("Connection will be made to server to fetch details like artists and tags for local comics in the included directories")
            .setView(view)
            .setPositiveButton("Continue") { dialogInterface: DialogInterface, i: Int ->
                val showDialogAgain = !(checkbox.isChecked)
                storeSyncDialogPreference(showDialogAgain)

                viewModel.fetchMetadataAll()
            }
            .setNegativeButton("Back") { dialogInterface: DialogInterface, i: Int ->
                val showDialogAgain = !(checkbox.isChecked)
                storeSyncDialogPreference(showDialogAgain)
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun storeSyncDialogPreference(status: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("com.flamyoad.tsukiviewer", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("show_dialog_before_sync", status)
        editor.apply()
    }

    private fun shouldShowSyncDialog(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("com.flamyoad.tsukiviewer", Context.MODE_PRIVATE)
//        PreferenceManager.getDefaultSharedPreferences(requireContext())
        return sharedPreferences.getBoolean("show_dialog_before_sync", true)
    }

    private fun openSortDialog() {
        val dialog = SortDoujinDialog()
        dialog.show(childFragmentManager, "sortdialog")
    }

    private fun openSearchActivity() {
        val intent = Intent(context, SearchActivity::class.java)
        startActivity(intent)
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    companion object {
        @JvmStatic
        fun newInstance(): LocalDoujinsFragment {
            return LocalDoujinsFragment()
        }

        const val APPBAR_TITLE = "Local Storage"
    }

    inner class ActionModeCallback: ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_bookmark -> {
                    val dialog = BookmarkGroupDialog.newInstance()
                    dialog.show(childFragmentManager, ADD_BOOKMARK_DIALOG)
                }
                R.id.action_edit -> {}
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
            requireActivity().window.statusBarColor = statusBarColor // Restores the original status bar color

            adapter.actionModeEnabled = false
            viewModel.clearSelectedDoujins()
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

}
