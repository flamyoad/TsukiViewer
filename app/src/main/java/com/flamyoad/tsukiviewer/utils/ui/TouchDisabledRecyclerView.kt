package com.flamyoad.tsukiviewer.utils.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView


class TouchDisabledRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {}
    
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return false
    }
}