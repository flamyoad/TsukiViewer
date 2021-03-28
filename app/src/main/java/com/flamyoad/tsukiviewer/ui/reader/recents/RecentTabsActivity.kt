package com.flamyoad.tsukiviewer.ui.reader.recents

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.RecentTabsAdapter
import com.flamyoad.tsukiviewer.model.RecentTab
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabFragment
import kotlinx.android.synthetic.main.activity_recent_tabs.*

class RecentTabsActivity : AppCompatActivity() {

    private val viewModel: RecentTabsViewModel by viewModels()

    private var touchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_tabs)
        initReaderHistory()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initReaderHistory() {
        val currentTabId = intent.getLongExtra(ReaderTabFragment.TAB_ID, -1)

        val tabAdapter = RecentTabsAdapter(this::onRecentTabClick, this::startDrag, currentTabId)
        tabAdapter.setHasStableIds(true)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        listTabs.apply {
            adapter = tabAdapter
            layoutManager = linearLayoutManager
        }

        val touchHelperCallback = RecentTabTouchHelperCallback(tabAdapter, currentTabId, viewModel::removeRecentTab)
        touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper?.attachToRecyclerView(listTabs)

        viewModel.tabList.observe(this, Observer {
            tabAdapter.submitList(it)
            val position = it.indexOfFirst { tab -> tab.id == currentTabId }
            linearLayoutManager.scrollToPosition(position)
        })
    }

    fun startDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    private fun onRecentTabClick(tab: RecentTab) {
        val result = Intent()
        result.putExtra(CHOSEN_TAB_ID, tab.id)
        setResult(RESULT_OK, result)
        finish()
    }

    companion object {
        const val CHOSEN_TAB_ID = "chosen_tab_id"
    }
}
