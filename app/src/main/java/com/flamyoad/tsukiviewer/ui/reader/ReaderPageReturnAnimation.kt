package com.flamyoad.tsukiviewer.ui.reader

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlin.math.max
import kotlin.math.min

/** Draws the page above the reader while its surface fades to the overview underneath. */
internal class ReaderPageReturnAnimation private constructor(private val animator: ValueAnimator) {
    fun cancel() = animator.cancel()

    companion object {
        fun start(
            page: SubsamplingScaleImageView,
            reader: View,
            thumbnailView: ImageView,
            onEnd: () -> Unit
        ): ReaderPageReturnAnimation? {
            val container = reader.parent as? ViewGroup ?: return null
            val visiblePage = Rect()
            val thumbnailBounds = Rect()
            if (!page.isReady || !page.getLocalVisibleRect(visiblePage) ||
                !thumbnailView.isAttachedToWindow || thumbnailView.drawable == null ||
                !thumbnailView.getLocalVisibleRect(thumbnailBounds)
            ) return null

            val pageLocation = IntArray(2).also(page::getLocationOnScreen)
            val readerLocation = IntArray(2).also(reader::getLocationOnScreen)
            val thumbnailLocation = IntArray(2).also(thumbnailView::getLocationOnScreen)
            visiblePage.offset(pageLocation[0], pageLocation[1])
            thumbnailBounds.offset(thumbnailLocation[0], thumbnailLocation[1])
            val topLeft = page.sourceToViewCoord(0f, 0f) ?: return null
            val bottomRight = page.sourceToViewCoord(page.sWidth.toFloat(), page.sHeight.toFloat())
                ?: return null
            val imageOnScreen = RectF(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y).apply {
                offset(pageLocation[0].toFloat(), pageLocation[1].toFloat())
            }
            val snapshotBounds = Rect()
            imageOnScreen.roundOut(snapshotBounds)
            if (!snapshotBounds.intersect(visiblePage)) return null

            val bitmap = Bitmap.createBitmap(
                snapshotBounds.width(), snapshotBounds.height(), Bitmap.Config.ARGB_8888
            )
            page.draw(Canvas(bitmap).apply {
                translate(
                    (pageLocation[0] - snapshotBounds.left).toFloat(),
                    (pageLocation[1] - snapshotBounds.top).toFloat()
                )
            })

            val startImage = RectF(snapshotBounds).apply {
                offset(-readerLocation[0].toFloat(), -readerLocation[1].toFloat())
            }
            val thumbnailClip = RectF(thumbnailBounds).apply {
                offset(-readerLocation[0].toFloat(), -readerLocation[1].toFloat())
            }
            val thumbnailLeft = (thumbnailLocation[0] - readerLocation[0]).toFloat()
            val thumbnailTop = (thumbnailLocation[1] - readerLocation[1]).toFloat()
            val thumbnail = RectF(
                thumbnailLeft, thumbnailTop,
                thumbnailLeft + thumbnailView.width, thumbnailTop + thumbnailView.height
            )
            val widthScale = thumbnail.width() / imageOnScreen.width()
            val heightScale = thumbnail.height() / imageOnScreen.height()
            val scale = if (thumbnailView.scaleType == ImageView.ScaleType.CENTER_CROP) {
                max(widthScale, heightScale)
            } else {
                min(widthScale, heightScale)
            }
            val left = thumbnail.centerX() - imageOnScreen.width() * scale / 2f
            val top = thumbnail.centerY() - imageOnScreen.height() * scale / 2f
            val endImage = RectF(
                left + (snapshotBounds.left - imageOnScreen.left) * scale,
                top + (snapshotBounds.top - imageOnScreen.top) * scale,
                left + (snapshotBounds.right - imageOnScreen.left) * scale,
                top + (snapshotBounds.bottom - imageOnScreen.top) * scale
            )

            val overlay = PageOverlay(reader, bitmap, startImage, endImage, thumbnailClip)
            val overlayLocation = IntArray(2).also(container::getLocationOnScreen)
            container.overlay.add(overlay)
            val x = readerLocation[0] - overlayLocation[0]
            val y = readerLocation[1] - overlayLocation[1]
            overlay.layout(x, y, x + reader.width, y + reader.height)
            page.alpha = 0f
            val thumbnailAlpha = thumbnailView.alpha
            thumbnailView.alpha = 0f
            var cancelled = false

            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 280L
                interpolator = PathInterpolator(0.2f, 0f, 0f, 1f)
                addUpdateListener {
                    val progress = it.animatedValue as Float
                    reader.alpha = 1f - progress
                    overlay.progress = progress
                    overlay.invalidate()
                }
                doOnCancel { cancelled = true }
                doOnEnd {
                    container.overlay.remove(overlay)
                    thumbnailView.alpha = thumbnailAlpha
                    if (cancelled) {
                        page.alpha = 1f
                        reader.alpha = 1f
                    } else {
                        onEnd()
                    }
                }
            }
            animator.start()
            return ReaderPageReturnAnimation(animator)
        }
    }

    private class PageOverlay(
        reader: View,
        private val bitmap: Bitmap,
        private val start: RectF,
        private val end: RectF,
        private val thumbnail: RectF
    ) : View(reader.context) {
        var progress = 0f
        private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        private val image = RectF()
        private val clip = RectF()

        override fun onDraw(canvas: Canvas) {
            interpolate(start, end, image)
            interpolate(start, thumbnail, clip)
            canvas.save()
            canvas.clipRect(clip)
            canvas.drawBitmap(bitmap, null, image, paint)
            canvas.restore()
        }

        private fun interpolate(from: RectF, to: RectF, out: RectF) {
            out.set(
                from.left + (to.left - from.left) * progress,
                from.top + (to.top - from.top) * progress,
                from.right + (to.right - from.right) * progress,
                from.bottom + (to.bottom - from.bottom) * progress
            )
        }
    }
}
