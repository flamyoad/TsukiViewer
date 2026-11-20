package com.flamyoad.tsukiviewer.utils.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class GridItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {

        outRect.set(0, 0, 0, 0)
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val params = view.layoutParams as? GridLayoutManager.LayoutParams
        val manager = parent.layoutManager as? GridLayoutManager
        val column = params?.spanIndex ?: (position % spanCount)
        val endColumn = column + (params?.spanSize ?: 1)
        val firstRow = position < spanCount && (manager?.spanSizeLookup?.getSpanGroupIndex(position, spanCount)
            ?: (position / spanCount)) == 0
        val startOffset = column * spacing / spanCount
        val endOffset = endColumn * spacing / spanCount
        if (includeEdge) {
            outRect.left = spacing - startOffset
            outRect.right = endOffset
            if (firstRow) outRect.top = spacing
            outRect.bottom = spacing
        } else {
            outRect.left = startOffset
            outRect.right = spacing - endOffset
            if (!firstRow) outRect.top = spacing
        }
    }
}
