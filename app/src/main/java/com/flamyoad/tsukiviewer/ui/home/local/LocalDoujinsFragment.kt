package com.flamyoad.tsukiviewer.ui.home.local

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.tsukiviewer.BaseFragment
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_local_doujins.*

class LocalDoujinsFragment : BaseFragment() {

    private val viewModel: LocalDoujinViewModel by activityViewModels()

    private lateinit var adapter: LocalDoujinsAdapter

    private lateinit var syncProgressBar: ProgressBar

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
        val menuItem = menu.findItem(R.id.progress_bar_sync)
        val view = menuItem.actionView

        syncProgressBar = view.findViewById(R.id.progressBarSync)
        syncProgressBar.visibility = View.GONE

        viewModel.isSyncing().observe(viewLifecycleOwner, Observer { stillSynchronizing ->
            if (stillSynchronizing) {
                syncProgressBar.visibility = View.VISIBLE
            } else {
                syncProgressBar.visibility = View.GONE
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
            R.id.action_sort_by_name -> {
                adapter.sortByName()
            }

            R.id.action_sort_by_date -> {
                adapter.sortByDate()
            }

            R.id.action_sort_by_path -> {
                adapter.sortByPath()
            }
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
        }
        return true
    }

    override fun onResume() {
        super.onResume()
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
        val sharedPreferences = requireContext().getSharedPreferences("showSyncDialog", 0)
        val editor = sharedPreferences.edit()

        editor.putBoolean("status", status)
        editor.apply()
    }

    private fun shouldShowSyncDialog(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("showSyncDialog", 0)
//        PreferenceManager.getDefaultSharedPreferences(requireContext())
        return sharedPreferences.getBoolean("status", true)
    }

    private fun initRecyclerView() {
        adapter = LocalDoujinsAdapter()
        adapter.setHasStableIds(true)
        val gridLayoutManager = GridLayoutManager(context, 2)

        listLocalDoujins.adapter = adapter
        listLocalDoujins.layoutManager = gridLayoutManager

        val itemDecoration = GridItemDecoration(2, 4, includeEdge = true)

        listLocalDoujins.addItemDecoration(itemDecoration)

        listLocalDoujins.setHasFixedSize(true)

        viewModel.doujinList().observe(viewLifecycleOwner, Observer { newList ->
            adapter.setList(newList)
        })
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

}
