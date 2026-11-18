package com.flamyoad.tsukiviewer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.ui.reader.ReaderMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class VerticalStripSidebarTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun verticalDragScrollsThePageListWhenTheSidebarIsOpenAndThePageIsZoomed() {
        val context = instrumentation.targetContext
        val preferences = MyAppPreference.getInstance(context)
        val previousMode = preferences.getDefaultReaderMode()
        val directory = File(context.cacheDir, "strip-sidebar-${System.nanoTime()}").apply { mkdirs() }
        val dao = AppDatabase.getInstance(context).recentTabDao()
        try {
            val bitmap = Bitmap.createBitmap(1800, 2400, Bitmap.Config.ARGB_8888)
            try {
                repeat(4) { page ->
                    File(directory, "$page.jpg").outputStream().use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
                    }
                }
            } finally {
                bitmap.recycle()
            }

            preferences.setDefaultReaderMode(ReaderMode.VerticalStrip)
            val intent = Intent(context, ReaderActivity::class.java)
                .putExtra(DoujinImagesAdapter.DIRECTORY_PATH, directory.absolutePath)
            ActivityScenario.launch<ReaderActivity>(intent).use { reader ->
                lateinit var activity: ReaderActivity
                reader.onActivity { activity = it }
                await("Reader page did not load") { photo(activity)?.isReady == true }
                reader.onActivity {
                    assertTrue(it.findViewById<View>(R.id.txtCurrentPageNumber).performClick())
                }
                await("Sidebar or resized page did not load") {
                    activity.findViewById<View>(R.id.pageSidebar)?.width?.let { it > 0 } == true &&
                        photo(activity)?.isReady == true
                }

                var originalScale = 0f
                reader.onActivity {
                    val image = requireNotNull(photo(it))
                    originalScale = image.scale
                    image.setScaleAndCenter(originalScale * 2, PointF(image.sWidth / 2f, image.sHeight / 2f))
                }
                await("Image did not zoom") { requireNotNull(photo(activity)).scale > originalScale * 1.5f }

                val bounds = Rect()
                var initialOffset = 0
                reader.onActivity {
                    assertTrue(requireNotNull(photo(it)).getGlobalVisibleRect(bounds))
                    initialOffset = it.findViewById<RecyclerView>(R.id.listImages).computeVerticalScrollOffset()
                }
                dragUp(bounds)
                await("The zoomed image intercepted the vertical drag") {
                    activity.findViewById<RecyclerView>(R.id.listImages).computeVerticalScrollOffset() > initialOffset
                }
            }
        } finally {
            preferences.setDefaultReaderMode(previousMode)
            runBlocking { dao.getByPath(directory.absolutePath)?.let(dao::delete) }
            directory.deleteRecursively()
        }
    }

    private fun photo(activity: ReaderActivity): SubsamplingScaleImageView? {
        val list = activity.findViewById<RecyclerView>(R.id.listImages) ?: return null
        val layout = list.layoutManager as? LinearLayoutManager ?: return null
        val firstPage = layout.findFirstVisibleItemPosition()
        return list.findViewHolderForAdapterPosition(firstPage)?.itemView?.findViewById(R.id.photoView)
    }

    private fun dragUp(bounds: Rect) {
        val downTime = SystemClock.uptimeMillis()
        val x = bounds.exactCenterX()
        val startY = bounds.top + bounds.height() * 0.65f
        for (step in 0..12) {
            val action = when (step) {
                0 -> MotionEvent.ACTION_DOWN
                12 -> MotionEvent.ACTION_UP
                else -> MotionEvent.ACTION_MOVE
            }
            val y = startY - bounds.height() * 0.25f * step / 12
            val event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), action, x, y, 0)
            event.source = InputDevice.SOURCE_TOUCHSCREEN
            try {
                instrumentation.sendPointerSync(event)
            } finally {
                event.recycle()
            }
            SystemClock.sleep(16)
        }
    }

    private fun await(message: String, condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        do {
            var met = false
            instrumentation.runOnMainSync { met = condition() }
            if (met) return
            SystemClock.sleep(50)
        } while (SystemClock.uptimeMillis() < deadline)
        throw AssertionError(message)
    }
}
