package com.flamyoad.tsukiviewer.ui.reader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder
import com.davemorrissey.labs.subscaleview.decoder.SkiaImageDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Draws the current GIF frame in the same image coordinates as the tiled still page. */
class ReaderPageImageView(context: Context, attrs: AttributeSet) :
    SubsamplingScaleImageView(context, attrs) {

    private var gifFile: File? = null
    private var check: Job? = null
    private var request: CustomTarget<GifDrawable>? = null
    private var animation: GifDrawable? = null
    private var wantsPlayback = false
    private var attemptedPlayback = false
    private var playbackOverride: Boolean? = null
    private val shouldPlay: Boolean
        get() = playbackOverride ?: MyAppPreference.getInstance(context).prefs
            .getBoolean(PreferenceKeys.READER_GIF_AUTOPLAY, true)
    private val frameOrigin = PointF()

    fun setPage(file: File) {
        stopAnimation()
        val newGif = file.takeIf { it.extension.equals("gif", ignoreCase = true) }
        if (newGif != gifFile) playbackOverride = null
        gifFile = newGif
        if (newGif != null) {
            setOnClickListener { toggleGifPlayback() }
        } else {
            setOnClickListener(null)
            isClickable = false
        }
        updatePlaybackDescription()
        val source = ImageSource.uri(Uri.fromFile(file))
        if (gifFile != null) {
            setBitmapDecoderFactory { ImageDecoder { _, _ -> decodeGifPreview(file) } }
            source.tilingDisabled()
        } else {
            setBitmapDecoderFactory { SkiaImageDecoder() }
        }
        setImage(source)
    }

    private fun decodeGifPreview(file: File): Bitmap {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, options)
        val width = options.outWidth
        val height = options.outHeight
        if (width <= 0 || height <= 0 || width.toLong() * height > MAX_PREVIEW_SOURCE_PIXELS) {
            throw IOException("GIF preview dimensions are unsupported")
        }
        options.inSampleSize = sampleSize(width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file.path, options)
            ?: throw IOException("Could not decode GIF preview")
    }

    fun startAnimation() {
        wantsPlayback = true
        maybeStartAnimation()
    }

    private fun toggleGifPlayback() {
        if (!wantsPlayback || gifFile == null) return
        val drawable = animation
        if (drawable != null && !drawable.isRunning) {
            val manuallyPaused = playbackOverride == false
            playbackOverride = true
            if (manuallyPaused) drawable.start() else drawable.startFromFirstFrame()
        } else if (shouldPlay) {
            playbackOverride = false
            if (drawable == null) releaseAnimation() else drawable.stop()
        } else {
            playbackOverride = true
            maybeStartAnimation()
        }
        updatePlaybackDescription()
    }

    private fun updatePlaybackDescription() {
        ViewCompat.setStateDescription(this, gifFile?.let {
            context.getString(if (shouldPlay) R.string.reader_gif_playing else R.string.reader_gif_paused)
        })
    }

    private fun maybeStartAnimation() {
        val file = gifFile ?: return
        if (!wantsPlayback || !shouldPlay || !isAttachedToWindow || attemptedPlayback) return
        val scope = findViewTreeLifecycleOwner()?.lifecycleScope ?: return
        attemptedPlayback = true
        check = scope.launch {
            val data = withContext(Dispatchers.IO) { readSafeGif(file) }
            check = null
            if (data != null) loadAnimation(data)
        }
    }

    fun stopAnimation() {
        wantsPlayback = false
        releaseAnimation()
    }

    private fun releaseAnimation() {
        check?.cancel()
        check = null
        request?.let { Glide.with(context.applicationContext).clear(it) }
        request = null
        attemptedPlayback = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        maybeStartAnimation()
    }

    override fun onDetachedFromWindow() {
        releaseAnimation()
        super.onDetachedFromWindow()
    }

    private fun loadAnimation(data: ByteBuffer) {
        data.order(ByteOrder.LITTLE_ENDIAN)
        val width = data.getShort(6).toInt() and 0xffff
        val height = data.getShort(8).toInt() and 0xffff
        val sample = sampleSize(width, height).coerceAtMost(Integer.highestOneBit(minOf(width, height)))
        val target = object : CustomTarget<GifDrawable>(width / sample, height / sample) {
            override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                animation = resource
                resource.callback = this@ReaderPageImageView
                resource.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
                resource.setLoopCount(GifDrawable.LOOP_INTRINSIC)
                resource.start()
                invalidate()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                animation?.stop()
                animation?.callback = null
                animation = null
                invalidate()
            }
        }
        request = target
        Glide.with(context.applicationContext)
            .asGif()
            .load(data.array())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(target)
    }

    override fun verifyDrawable(who: Drawable): Boolean = who === animation || super.verifyDrawable(who)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawable = animation ?: return
        if (!isReady) return
        val origin = sourceToViewCoord(0f, 0f, frameOrigin) ?: return
        val width = sWidth * scale
        val height = sHeight * scale
        val save = canvas.save()
        canvas.translate(origin.x, origin.y)
        canvas.scale(width / drawable.intrinsicWidth, height / drawable.intrinsicHeight)
        // Transparent GIF frames should reveal the reader background, not the still preview.
        canvas.clipRect(drawable.bounds)
        canvas.drawColor(Color.BLACK)
        drawable.draw(canvas)
        canvas.restoreToCount(save)
    }

    private suspend fun readSafeGif(file: File): ByteBuffer? {
        try {
            val size = file.length()
            if (size !in 13L..MAX_GIF_BYTES) return null
            val bytes = ByteArray(size.toInt())
            DataInputStream(file.inputStream().buffered()).use { stream ->
                var offset = 0
                while (offset < bytes.size) {
                    currentCoroutineContext().ensureActive()
                    val count = minOf(64 * 1024, bytes.size - offset)
                    stream.readFully(bytes, offset, count)
                    offset += count
                }
                if (stream.read() != -1) return null
            }
            return ByteBuffer.wrap(bytes).takeIf { canAnimateGif(it.duplicate()) }
        } catch (_: IOException) {
            return null
        }
    }

    private fun sampleSize(width: Int, height: Int): Int {
        var sample = 1
        while (maxOf(width, height) / sample > TARGET_SIZE) sample *= 2
        return sample
    }

    private companion object {
        const val MAX_GIF_BYTES = 8L * 1024 * 1024
        const val MAX_PREVIEW_SOURCE_PIXELS = 16_000_000L
        const val TARGET_SIZE = 1024
    }
}
