package com.flamyoad.tsukiviewer.utils

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLinearLayoutManager: LinearLayoutManager {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, @RecyclerView.Orientation orientation: Int, reverseLayout: Boolean)
            : super(context, orientation, reverseLayout)

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    override fun canScrollVertically(): Boolean {
        return false
    }
}