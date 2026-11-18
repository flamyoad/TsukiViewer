package com.flamyoad.tsukiviewer.ui.reader

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class VerticalStripRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    var shouldPrioritizePageScroll: () -> Boolean = { false }

    private var prioritizePageScroll = false

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> prioritizePageScroll = shouldPrioritizePageScroll()
            MotionEvent.ACTION_POINTER_DOWN -> prioritizePageScroll = false
        }
        val handled = super.dispatchTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
            prioritizePageScroll = false
        }
        return handled
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (prioritizePageScroll && disallowIntercept) {
            // Keep the parent tab pager out of the gesture, but let this list intercept vertical drags.
            parent?.requestDisallowInterceptTouchEvent(true)
        } else {
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
        }
    }
}
