package com.flamyoad.tsukiviewer.ui.reader.recents

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.adapter.RecentTabsAdapter
import com.flamyoad.tsukiviewer.model.RecentTab

class RecentTabTouchHelperCallback(
    private val adapter: RecentTabsAdapter,
    private val onItemSwiped: (RecentTab) -> Unit) :
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.UP or ItemTouchHelper.DOWN)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = adapter.getRecentTab(viewHolder.bindingAdapterPosition)
        onItemSwiped(item)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
}