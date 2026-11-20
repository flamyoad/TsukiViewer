package com.flamyoad.tsukiviewer.ui.doujinpage

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import kotlin.math.roundToInt

class PageThumbnailTile(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    var heightPerColumnWidth = 1f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val spans = (layoutParams as? GridLayoutManager.LayoutParams)?.spanSize
            ?.coerceAtLeast(1) ?: 1
        val height = (MeasureSpec.getSize(widthMeasureSpec) * heightPerColumnWidth / spans).roundToInt()
        val tileHeightSpec = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            heightMeasureSpec
        } else {
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, tileHeightSpec)
    }
}
