package com.flamyoad.tsukiviewer.utils.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class PinchGridRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.recyclerview.R.attr.recyclerViewStyle
) : RecyclerView(context, attrs, defStyleAttr) {
    internal val gridPinch = GridPinchGesture(this)

    override fun dispatchTouchEvent(event: MotionEvent): Boolean =
        gridPinch.dispatchTouchEvent(event) { super.dispatchTouchEvent(it) }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        gridPinch.beforeLayout()
        super.onLayout(changed, left, top, right, bottom)
        gridPinch.afterLayout()
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean =
        gridPinch.drawChild(canvas, child) { super.drawChild(canvas, child, drawingTime) }

    override fun onDetachedFromWindow() {
        gridPinch.reset()
        super.onDetachedFromWindow()
    }
}

class PinchFastScrollRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FastScrollRecyclerView(context, attrs, defStyleAttr) {
    internal val gridPinch = GridPinchGesture(this)

    override fun dispatchTouchEvent(event: MotionEvent): Boolean =
        gridPinch.dispatchTouchEvent(event) { super.dispatchTouchEvent(it) }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        gridPinch.beforeLayout()
        super.onLayout(changed, left, top, right, bottom)
        gridPinch.afterLayout()
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean =
        gridPinch.drawChild(canvas, child) { super.drawChild(canvas, child, drawingTime) }

    override fun onDetachedFromWindow() {
        gridPinch.reset()
        super.onDetachedFromWindow()
    }
}

internal class GridPinchGesture(private val recycler: RecyclerView) {
    private var enabled = true
    private var maxSpanCount = 6
    private var updateSpacing: ((Int) -> Unit)? = null
    private var onSpanCommitted: ((Int) -> Unit)? = null
    private var decoration: GridItemDecoration? = null
    private var previousBounds: Map<Long, RectF>? = null

    fun configure(
        maxSpanCount: Int,
        spacing: Int,
        includeEdge: Boolean,
        enabled: Boolean = true,
        onSpanCommitted: (Int) -> Unit
    ) {
        reset()
        this.maxSpanCount = maxSpanCount
        this.enabled = enabled
        val manager = recycler.layoutManager as GridLayoutManager
        fun updateSpacing(spanCount: Int) {
            decoration?.let(recycler::removeItemDecoration)
            val newDecoration = GridItemDecoration(spanCount, spacing, includeEdge)
            recycler.addItemDecoration(newDecoration)
            decoration = newDecoration
        }
        updateSpacing(manager.spanCount)
        this.updateSpacing = ::updateSpacing
        this.onSpanCommitted = onSpanCommitted
    }

    private var consumingPinch = false
    private var focusX = 0f
    private var focusY = 0f
    private var gestureScale = 1f
    private var pendingReflow = false
    private var reflowAnimator: ValueAnimator? = null
    private var startBounds = emptyMap<Long, RectF>()
    private var reflowProgress = 1f
    private val drawnBounds = RectF()

    private val detector = ScaleGestureDetector(
        recycler.context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val factor = detector.scaleFactor
                if (factor.isFinite() && factor > 0f) {
                    gestureScale *= factor
                    focusX = detector.focusX
                    focusY = detector.focusY
                }
                return true
            }
        }
    ).apply {
        isQuickScaleEnabled = false
        isStylusScaleEnabled = false
    }

    fun dispatchTouchEvent(event: MotionEvent, dispatch: (MotionEvent) -> Boolean): Boolean {
        if (!enabled) return dispatch(event)
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_POINTER_DOWN && !consumingPinch) {
            val cancel = MotionEvent.obtain(event)
            cancel.action = MotionEvent.ACTION_CANCEL
            dispatch(cancel)
            cancel.recycle()
            recycler.stopScroll()
            consumingPinch = true
            gestureScale = 1f
            recycler.parent?.requestDisallowInterceptTouchEvent(true)
        }

        detector.onTouchEvent(event)
        if (!consumingPinch) return dispatch(event)

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            consumingPinch = false
            if (action == MotionEvent.ACTION_UP) commitPinch()
            recycler.parent?.requestDisallowInterceptTouchEvent(false)
        }
        return true
    }

    private fun commitPinch() {
        val manager = recycler.layoutManager as? GridLayoutManager ?: return
        if (recycler.childCount == 0) return

        val columnChange = when {
            gestureScale <= 1f / RELEASE_SCALE_THRESHOLD -> 1
            gestureScale >= RELEASE_SCALE_THRESHOLD -> -1
            else -> return
        }
        val span = (manager.spanCount + columnChange).coerceIn(MIN_SPAN_COUNT, maxSpanCount)
        if (span == manager.spanCount) return
        reflow(manager, span)
        onSpanCommitted?.invoke(span)
    }

    private fun reflow(manager: GridLayoutManager, span: Int) {
        val focused = (recycler.childCount - 1 downTo 0).firstNotNullOfOrNull { index ->
            val child = recycler.getChildAt(index)
            displayedBounds(child, drawnBounds)
            child.takeIf { drawnBounds.contains(focusX, focusY) }
        }
        val position = focused?.let(recycler::getChildAdapterPosition)
            ?.takeUnless { it == RecyclerView.NO_POSITION } ?: manager.findFirstVisibleItemPosition()
        pendingReflow = true
        resizeGrid(manager, span, position)
    }

    fun restoreSpanCount(savedSpan: Int) {
        val manager = recycler.layoutManager as? GridLayoutManager ?: return
        val span = savedSpan.coerceIn(MIN_SPAN_COUNT, maxSpanCount)
        if (span == manager.spanCount) return
        reset()
        resizeGrid(manager, span, manager.findFirstVisibleItemPosition())
    }

    private fun resizeGrid(manager: GridLayoutManager, span: Int, anchorPosition: Int) {
        val anchor = manager.findViewByPosition(anchorPosition)
        val offset = anchor?.let { manager.getDecoratedTop(it) - recycler.paddingTop }
        manager.spanCount = span
        if (offset != null) manager.scrollToPositionWithOffset(anchorPosition, offset)
        updateSpacing?.invoke(span)
    }

    fun beforeLayout() {
        if (!pendingReflow) return

        // A second pinch can land before the previous reflow finishes.
        previousBounds = buildMap {
            for (index in 0 until recycler.childCount) {
                val child = recycler.getChildAt(index)
                val id = recycler.getChildViewHolder(child).itemId
                if (id == RecyclerView.NO_ID) continue
                val bounds = RectF()
                displayedBounds(child, bounds)
                put(id, bounds)
            }
        }
    }

    fun afterLayout() {
        val bounds = previousBounds ?: return
        previousBounds = null
        pendingReflow = false
        reflowAnimator?.cancel()
        startBounds = bounds
        reflowProgress = 0f
        reflowAnimator = animate { progress ->
            reflowProgress = progress
            if (progress == 1f) startBounds = emptyMap()
        }
    }

    fun drawChild(canvas: Canvas, child: View, draw: () -> Boolean): Boolean {
        if (startBounds.isEmpty() || child.width == 0 || child.height == 0) {
            return draw()
        }
        displayedBounds(child, drawnBounds)
        val bounds = drawnBounds
        val checkpoint = canvas.save()
        canvas.translate(bounds.left - child.left, bounds.top - child.top)
        canvas.scale(
            bounds.width() / child.width,
            bounds.height() / child.height,
            child.left.toFloat(),
            child.top.toFloat()
        )
        val drawn = draw()
        canvas.restoreToCount(checkpoint)
        return drawn
    }

    private fun displayedBounds(child: View, out: RectF) {
        val start = startBounds[recycler.getChildViewHolder(child).itemId]
        val left = child.left.toFloat()
        val top = child.top.toFloat()
        val right = child.right.toFloat()
        val bottom = child.bottom.toFloat()
        if (start == null) {
            out.set(left, top, right, bottom)
            return
        }
        out.set(
            start.left + (left - start.left) * reflowProgress,
            start.top + (top - start.top) * reflowProgress,
            start.right + (right - start.right) * reflowProgress,
            start.bottom + (bottom - start.bottom) * reflowProgress
        )
    }

    fun reset() {
        if (consumingPinch) recycler.parent?.requestDisallowInterceptTouchEvent(false)
        consumingPinch = false
        gestureScale = 1f
        reflowAnimator?.cancel()
        pendingReflow = false
        previousBounds = null
        startBounds = emptyMap()
        recycler.invalidate()
    }

    private fun animate(update: (Float) -> Unit): ValueAnimator =
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 180L
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                update(it.animatedValue as Float)
                recycler.invalidate()
            }
            start()
        }

    companion object {
        const val MIN_SPAN_COUNT = 2

        fun maxSpanCount(landscape: Boolean): Int = if (landscape) 8 else 6

        private const val RELEASE_SCALE_THRESHOLD = 1.2f
    }
}
