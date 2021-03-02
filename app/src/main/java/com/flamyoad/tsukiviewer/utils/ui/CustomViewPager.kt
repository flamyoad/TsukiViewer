package com.flamyoad.tsukiviewer.utils.ui

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager


class CustomViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        try {
            val numChildren = childCount
            for (i in 0 until numChildren) {
                val child = getChildAt(i)
                if (child != null) {
                    child.measure(
                        widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )
                    val h = child.measuredHeight
                    heightMeasureSpec = Math.max(
                        heightMeasureSpec,
                        MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}