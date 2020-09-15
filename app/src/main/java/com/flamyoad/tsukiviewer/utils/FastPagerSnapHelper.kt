package com.flamyoad.tsukiviewer.utils

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider

/*
    The original PagerSnapHelper has some delays when scrolling. It is very noticable when the users are swiping quickly
    This modified version of PagerSnapHelper changes the value of MAX_SCROLL_ON_FLING_DURATION from 100ms to 10ms
 */
class FastPagerSnapHelper(private val mRecyclerView: RecyclerView)
        : PagerSnapHelper() {

    private val MAX_SCROLL_ON_FLING_DURATION = 10 // ms, Original value in the source file is 100ms
    private val MILLISECONDS_PER_INCH = 100f

    override fun createScroller(layoutManager: RecyclerView.LayoutManager): RecyclerView.SmoothScroller? {
        return if (layoutManager !is ScrollVectorProvider) {
            null
        } else object : LinearSmoothScroller(mRecyclerView.context) {
            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                val snapDistances = calculateDistanceToFinalSnap(
                    mRecyclerView.layoutManager!!,
                    targetView
                )
                val dx = snapDistances!![0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(
                    Math.max(
                        Math.abs(dx), Math.abs(dy)
                    )
                )
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }

            override fun calculateTimeForScrolling(dx: Int): Int {
                return Math.min(
                    MAX_SCROLL_ON_FLING_DURATION,
                    super.calculateTimeForScrolling(dx)
                )
            }
        }
    }
}
