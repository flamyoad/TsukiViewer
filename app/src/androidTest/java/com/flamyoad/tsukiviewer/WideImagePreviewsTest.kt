package com.flamyoad.tsukiviewer

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter.ItemType
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity
import com.flamyoad.tsukiviewer.ui.doujinpage.GridViewStyle
import com.flamyoad.tsukiviewer.utils.ui.PinchFastScrollRecyclerView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class WideImagePreviewsTest {
    @Test
    fun portraitPinchShrinksASpreadToOneColumnAndRestoresIt() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preference = MyAppPreference.getInstance(context)
        val previousStyle = preference.getDefaultViewStyle()
        val previousEnabled = preference.prefs.getBoolean(PreferenceKeys.WIDE_IMAGE_PREVIEWS, false)
        val previousColumns = preference.getXmlGridSpan(MyAppPreference.XmlGrid.IMAGES, ItemType.Grid, false, 3)
        val directory = File(context.cacheDir, "wide-preview-${System.nanoTime()}").apply { mkdirs() }
        try {
            for (page in 1..12) {
                val bitmap = Bitmap.createBitmap(if (page == 2) 160 else 80, 100, Bitmap.Config.RGB_565)
                File(directory, "page-$page.png").outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                bitmap.recycle()
            }
            preference.setDefaultViewStyle(GridViewStyle.Grid)
            preference.put(PreferenceKeys.WIDE_IMAGE_PREVIEWS, true)
            preference.setXmlGridSpan(MyAppPreference.XmlGrid.IMAGES, ItemType.Grid, false, 3)
            val intent = Intent(context, DoujinDetailsActivity::class.java)
                .putExtra(DoujinDetailsActivity.OPEN_OVERVIEW, true)
                .putExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH, directory.path)

            ActivityScenario.launch<DoujinDetailsActivity>(intent).use { scenario ->
                scenario.onActivity {
                    it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    it.findViewById<ViewPager>(R.id.viewpager).setCurrentItem(1, false)
                }
                awaitSpread(scenario, columns = 3, span = 2)
                scenario.onActivity { pinch(it.findViewById(R.id.listImages), expand = true) }
                awaitSpread(scenario, columns = 2, span = 1)
                scenario.onActivity { pinch(it.findViewById(R.id.listImages), expand = false) }
                awaitSpread(scenario, columns = 3, span = 2)
            }
        } finally {
            preference.setDefaultViewStyle(previousStyle)
            preference.put(PreferenceKeys.WIDE_IMAGE_PREVIEWS, previousEnabled)
            preference.setXmlGridSpan(MyAppPreference.XmlGrid.IMAGES, ItemType.Grid, false, previousColumns)
            directory.deleteRecursively()
        }
    }

    private fun awaitSpread(scenario: ActivityScenario<DoujinDetailsActivity>, columns: Int, span: Int) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        var actual = Triple(0, 0, null as ImageView.ScaleType?)
        do {
            scenario.onActivity { activity ->
                val grid = activity.findViewById<PinchFastScrollRecyclerView>(R.id.listImages)
                val manager = grid?.layoutManager as? GridLayoutManager
                val page = manager?.findViewByPosition(1)
                actual = Triple(
                    manager?.spanCount ?: 0,
                    (page?.layoutParams as? GridLayoutManager.LayoutParams)?.spanSize ?: 0,
                    page?.findViewById<ImageView>(R.id.imageView)?.scaleType
                )
            }
            if (actual == Triple(columns, span, ImageView.ScaleType.FIT_CENTER)) return
            SystemClock.sleep(30)
        } while (SystemClock.uptimeMillis() < deadline)
        assertEquals(Triple(columns, span, ImageView.ScaleType.FIT_CENTER), actual)
    }

    private fun pinch(grid: PinchFastScrollRecyclerView, expand: Boolean) {
        val page = requireNotNull(grid.layoutManager?.findViewByPosition(1))
        val centerX = page.left + page.width / 2f
        val centerY = page.top + page.height / 2f
        val downTime = SystemClock.uptimeMillis()
        val pointers = Array(2) { index ->
            MotionEvent.PointerProperties().apply {
                id = index
                toolType = MotionEvent.TOOL_TYPE_FINGER
            }
        }
        fun send(action: Int, count: Int, distance: Float) {
            val coords = Array(2) { index ->
                MotionEvent.PointerCoords().apply {
                    x = centerX + if (index == 0) -distance else distance
                    y = centerY
                    pressure = 1f
                    size = 1f
                }
            }
            val event = MotionEvent.obtain(
                downTime, SystemClock.uptimeMillis(), action, count, pointers, coords,
                0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0
            )
            grid.dispatchTouchEvent(event)
            event.recycle()
        }
        val distances = listOf(0.08f, 0.18f, 0.24f, 0.30f, 0.39f, 0.46f)
        val steps = (if (expand) distances else distances.reversed()).map { it * grid.width }
        val secondPointer = 1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT
        send(MotionEvent.ACTION_DOWN, 1, steps.first())
        send(MotionEvent.ACTION_POINTER_DOWN or secondPointer, 2, steps.first())
        for (distance in steps.drop(1)) send(MotionEvent.ACTION_MOVE, 2, distance)
        send(MotionEvent.ACTION_POINTER_UP or secondPointer, 2, steps.last())
        send(MotionEvent.ACTION_UP, 1, steps.last())
    }
}
