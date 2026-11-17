package com.flamyoad.tsukiviewer.ui.reader.recents

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.adapter.RecentTabsAdapter
import com.flamyoad.tsukiviewer.core.model.RecentTab

class RecentTabTouchHelperCallback(
    private val adapter: RecentTabsAdapter,
    private val onItemSwiped: (RecentTab) -> Unit,
    private val canSwipe: () -> Boolean
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun isItemViewSwipeEnabled(): Boolean = canSwipe()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.currentList.firstOrNull { it.id == viewHolder.itemId }?.let(onItemSwiped)
    }
}
