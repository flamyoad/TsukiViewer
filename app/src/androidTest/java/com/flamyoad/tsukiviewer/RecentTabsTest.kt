package com.flamyoad.tsukiviewer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.viewpager2.widget.ViewPager2
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.RecentTab
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.ui.reader.recents.RecentTabsActivity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RecentTabsTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun opensWithTheSelectedReaderTabVisibleInALongGrid() {
        val context = instrumentation.targetContext
        val dao = AppDatabase.getInstance(context).recentTabDao()
        val fixture = File(context.cacheDir, "recent-tabs-test-${System.nanoTime()}").apply { mkdirs() }
        val page = Bitmap.createBitmap(16, 24, Bitmap.Config.ARGB_8888)
        val tabs = mutableListOf<RecentTab>()
        val monitor = instrumentation.addMonitor(RecentTabsActivity::class.java.name, null, false)

        try {
            runBlocking {
                repeat(100) { index ->
                    val directory = File(fixture, "Test book $index").apply { mkdirs() }
                    val cover = File(directory, "1.png")
                    cover.outputStream().use { page.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    val tab = RecentTab(title = directory.name, dirPath = directory, thumbnail = cover)
                    tabs += tab.copy(id = dao.open(tab))
                }
            }

            val original = tabs[72]
            val selected = tabs[73]
            val intent = Intent(context, ReaderActivity::class.java)
                .putExtra(DoujinImagesAdapter.DIRECTORY_PATH, original.dirPath.absolutePath)
            ActivityScenario.launch<ReaderActivity>(intent).use { reader ->
                lateinit var readerActivity: ReaderActivity
                reader.onActivity { readerActivity = it }
                awaitOnMainThread("Reader pager did not load the test tabs") {
                    val pager = readerActivity.findViewById<ViewPager2>(R.id.viewPager)
                    pager.adapter?.getItemId(pager.currentItem) == original.id && currentTabButton(pager) != null
                }
                reader.onActivity { activity ->
                    val pager = activity.findViewById<ViewPager2>(R.id.viewPager)
                    val button = requireNotNull(currentTabButton(pager))
                    // A tap can arrive on the previous page's toolbar before the pager redraws.
                    pager.setCurrentItem(pager.currentItem + 1, false)
                    assertEquals(selected.id, pager.adapter?.getItemId(pager.currentItem))
                    assertTrue(button.performClick())
                }
                val switcher = requireNotNull(instrumentation.waitForMonitorWithTimeout(monitor, 5_000))
                val next = tabs[76]
                try {
                    val grid = switcher.findViewById<RecyclerView>(R.id.listTabs)
                    assertActiveCardVisible(grid, selected)
                    instrumentation.runOnMainSync {
                        val adapter = requireNotNull(grid.adapter)
                        val position = (0 until adapter.itemCount)
                            .first { adapter.getItemId(it) == next.id }
                        grid.scrollToPosition(position)
                    }
                    awaitOnMainThread("Could not reach the next tab") {
                        grid.findViewHolderForItemId(requireNotNull(next.id)) != null
                    }
                    instrumentation.runOnMainSync {
                        val holder = requireNotNull(grid.findViewHolderForItemId(requireNotNull(next.id)))
                        assertTrue(holder.itemView.performClick())
                    }
                    awaitOnMainThread("Reader did not switch to the tapped tab") {
                        val pager = readerActivity.findViewById<ViewPager2>(R.id.viewPager)
                        readerActivity.hasWindowFocus() &&
                            pager.adapter?.getItemId(pager.currentItem) == next.id &&
                            currentTabButton(pager) != null
                    }
                    val reopen = instrumentation.addMonitor(RecentTabsActivity::class.java.name, null, false)
                    try {
                        instrumentation.runOnMainSync {
                            val pager = readerActivity.findViewById<ViewPager2>(R.id.viewPager)
                            assertTrue(requireNotNull(currentTabButton(pager)).performClick())
                        }

                        val reopened = requireNotNull(instrumentation.waitForMonitorWithTimeout(reopen, 5_000))
                        try {
                            val reopenedGrid = reopened.findViewById<RecyclerView>(R.id.listTabs)
                            assertActiveCardVisible(reopenedGrid, next)
                            // Thumbnail updates can request more layouts after the first visible frame.
                            repeat(4) { relayout(reopenedGrid) }
                            assertActiveCardVisible(reopenedGrid, next)
                        } finally {
                            instrumentation.runOnMainSync { reopened.finish() }
                        }
                    } finally {
                        instrumentation.removeMonitor(reopen)
                    }
                } finally {
                    instrumentation.runOnMainSync { switcher.finish() }
                }
            }
        } finally {
            instrumentation.removeMonitor(monitor)
            tabs.forEach(dao::delete)
            fixture.deleteRecursively()
            page.recycle()
        }
    }

    private fun assertActiveCardVisible(grid: RecyclerView, tab: RecentTab) {
        awaitOnMainThread("The active tab was outside the grid viewport") {
            val card = grid.findViewHolderForItemId(requireNotNull(tab.id))?.itemView
            val visible = Rect()
            val label = grid.context.getString(R.string.reader_tabs_active, tab.title)
            card != null && card.contentDescription == label &&
                card.getGlobalVisibleRect(visible) && visible.height() == card.height
        }
    }

    private fun relayout(grid: RecyclerView) {
        val done = CountDownLatch(1)
        instrumentation.runOnMainSync {
            grid.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    grid.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    done.countDown()
                }
            })
            grid.requestLayout()
        }
        assertTrue("The grid did not lay out again", done.await(5, TimeUnit.SECONDS))
    }

    private fun currentTabButton(pager: ViewPager2): View? {
        val pages = pager.getChildAt(0) as RecyclerView
        return pages.findViewHolderForAdapterPosition(pager.currentItem)?.itemView
            ?.findViewById(R.id.btnTab)
    }

    private fun awaitOnMainThread(message: String, condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        var met = false
        do {
            instrumentation.runOnMainSync { met = condition() }
            if (met) return
            SystemClock.sleep(50)
        } while (SystemClock.uptimeMillis() < deadline)
        assertTrue(message, met)
    }
}
