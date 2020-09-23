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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_local_doujins.*

class LocalDoujinsFragment : BaseFragment() {

    private val viewModel: LocalDoujinViewModel by activityViewModels()

    private lateinit var adapter: LocalDoujinsAdapter

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
            } else {
                progressBar.visibility = View.GONE
                sortMenuItem.isVisible = true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_local_doujins_fragment, menu)
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

            R.id.action_sort_dialog -> {
                openSortDialog()
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        toast = Toast.makeText(context, "", Toast.LENGTH_LONG)

        viewModel.toastText().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                toast.setText(it)
                toast.show()
            }
        })

        viewModel.isSorting().observe(viewLifecycleOwner, Observer { stillSorting ->
            if (stillSorting) {
                listLocalDoujins.alpha = 0.6f
                listLocalDoujins.isEnabled = false
                sortingIndicator.visibility = View.VISIBLE
            } else {
                listLocalDoujins.alpha = 1f
                listLocalDoujins.isEnabled = true
                sortingIndicator.visibility = View.GONE
            }
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

    private fun initRecyclerView() {
        adapter = LocalDoujinsAdapter()
        adapter.setHasStableIds(true)

        // StateRestorationPolicy is in alpha stage
        adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }

        val gridLayoutManager = GridLayoutManager(context, spanCount)

        listLocalDoujins.adapter = adapter
        listLocalDoujins.layoutManager = gridLayoutManager

        val itemDecoration = GridItemDecoration(spanCount, 4, includeEdge = true)

        listLocalDoujins.addItemDecoration(itemDecoration)

        listLocalDoujins.setHasFixedSize(true)

        listLocalDoujins.itemAnimator = null

        viewModel.doujinList().observe(viewLifecycleOwner, Observer { newList ->
            adapter.setList(newList)
        })
    }

    private fun openSortDialog() {
        val dialog = SortDoujinDialog()
        dialog.show(childFragmentManager, "sortdialog")
    }

    private fun openSearchActivity() {
        val intent = Intent(context, SearchActivity::class.java)
        startActivity(intent)
    }

//    override fun makeSceneTransitionAnimation(view: View): ActivityOptionsCompat {
//        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//            requireActivity(),
//            view,
//            ViewCompat.getTransitionName(view) ?: ""
//        )
//        return options
//    }

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

}
