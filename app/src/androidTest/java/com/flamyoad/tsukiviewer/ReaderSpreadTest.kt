package com.flamyoad.tsukiviewer

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.ReaderSpreadPreferences
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.ui.reader.ReaderMode
import com.flamyoad.tsukiviewer.ui.reader.ReaderPageImageView
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabFragment
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ReaderSpreadTest {
    @Test
    fun rightToLeftSpreadKeepsItsSelectedPageWhenReturningToPortrait() {
        withReaderFixture(ReaderMode.HorizontalSwipe, automatic = true) { intent, files ->
            ActivityScenario.launch<ReaderActivity>(intent).use { scenario ->
                scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
                await(scenario, "Landscape window did not open") {
                    it.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                }
                scenario.onActivity {
                    assumeTrue(it.resources.configuration.screenWidthDp >= 720)
                }
                await(scenario, "Pages 2 and 3 should share a right-to-left spread") {
                    val second = boundsOf(it, files[1])
                    val third = boundsOf(it, files[2])
                    indicator(it) == "Page: 2–3 / 5" &&
                        second != null && third != null && second.left >= third.right
                }
                scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
                await(scenario, "Portrait should return to page 3") {
                    indicator(it) == "Page: 3 / 5" && boundsOf(it, files[2]) != null &&
                        boundsOf(it, files[1]) == null
                }
            }
        }
    }

    @Test
    fun dedicatedTwoPageModeCanBeSelectedFromVerticalStripInPortrait() {
        withReaderFixture(ReaderMode.VerticalStrip, automatic = false) { intent, _ ->
            ActivityScenario.launch<ReaderActivity>(intent).use { scenario ->
                scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
                await(scenario, "Portrait window did not open") {
                    it.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                }
                await(scenario, "Vertical strip did not open page 3") {
                    indicator(it) == "Page: 3 / 5"
                }
                scenario.onActivity {
                    assertEquals(1, sidebarSwipeAt(it, 0.5f))
                    assertTrue(it.findViewById<View>(R.id.bottomSheetOpener).performClick())
                    val option = it.findViewById<View>(R.id.btnTwoPage)
                    assertTrue(option.isShown)
                    assertTrue(option.performClick())
                }
                await(scenario, "Two-page mode did not open a spread in portrait") {
                    indicator(it) == "Page: 2–3 / 5"
                }
                await(scenario, "Hidden toolbar still reserves space for the spread", ::spreadFillsViewport)
                scenario.onActivity {
                    assertEquals(0, sidebarSwipeAt(it, 0.5f))
                    assertEquals(1, sidebarSwipeAt(it, 0.15f))
                    assertEquals(0, sidebarSwipeAt(it, 0.85f))
                    val toolbar = it.findViewById<View>(R.id.appBarLayout)
                    val modeMenu = it.findViewById<View>(R.id.readerModeDialog)
                    assertEquals(View.GONE, toolbar.visibility)
                    assertTrue(it.findViewById<View>(R.id.bottomSheetOpener).performClick())
                    assertTrue(toolbar.isShown)
                    assertTrue(modeMenu.isShown)
                }
                await(scenario, "Revealing controls resized the spread", ::spreadFillsViewport)
                scenario.onActivity {
                    val toolbar = it.findViewById<View>(R.id.appBarLayout)
                    val modeMenu = it.findViewById<View>(R.id.readerModeDialog)
                    val tab = it.supportFragmentManager.fragments.filterIsInstance<ReaderTabFragment>()
                        .single { fragment -> fragment.isResumed }
                    tab.toggleBottomSheet(View.INVISIBLE)
                    assertEquals(View.GONE, toolbar.visibility)
                    assertEquals(View.INVISIBLE, modeMenu.visibility)
                }
            }
        }
    }

    private fun withReaderFixture(
        mode: ReaderMode,
        automatic: Boolean,
        test: (Intent, List<File>) -> Unit
    ) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = MyAppPreference.getInstance(context)
        val previousMode = prefs.getDefaultReaderMode()
        val previousSpreads = ReaderSpreadPreferences.read(prefs.prefs)
        val directory = File(context.cacheDir, "spread-${System.nanoTime()}").apply { mkdirs() }
        val files = (1..5).map { File(directory, "page-$it.jpg") }
        val bitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.RGB_565)
        try {
            files.forEach { file ->
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it) }
            }
            prefs.setDefaultReaderMode(mode)
            ReaderSpreadPreferences(enabled = automatic, separateCover = true, rightToLeft = true).save(prefs.prefs)
            val intent = Intent(context, ReaderActivity::class.java)
                .putExtra(DoujinImagesAdapter.DIRECTORY_PATH, directory.absolutePath)
                .putExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 2)

            test(intent, files)
        } finally {
            bitmap.recycle()
            prefs.setDefaultReaderMode(previousMode)
            previousSpreads.save(prefs.prefs)
            val dao = AppDatabase.getInstance(context).recentTabDao()
            runBlocking { dao.getByPath(directory.absolutePath)?.let(dao::delete) }
            directory.deleteRecursively()
        }
    }

    private fun spreadFillsViewport(activity: ReaderActivity): Boolean {
        val tab = activity.supportFragmentManager.fragments.filterIsInstance<ReaderTabFragment>()
            .firstOrNull { it.isResumed }?.view ?: return false
        val imageContainer = tab.findViewById<View>(R.id.imageContainer)
        val pager = imageContainer.findViewById<View>(R.id.viewpager) ?: return false
        return tab.height > 0 && imageContainer.top == 0 && imageContainer.height == tab.height &&
            pager.top == 0 && pager.height == imageContainer.height
    }

    private fun sidebarSwipeAt(activity: ReaderActivity, fraction: Float): Int {
        val tab = activity.supportFragmentManager.fragments.filterIsInstance<ReaderTabFragment>()
            .single { it.isResumed }
        val bounds = Rect()
        check(requireNotNull(tab.view).getGlobalVisibleRect(bounds))
        return tab.sidebarSwipeDirection(bounds.left + bounds.width() * fraction, bounds.exactCenterY())
    }

    private fun indicator(activity: ReaderActivity): String? =
        activity.findViewById<TextView>(R.id.txtCurrentPageNumber)?.text?.toString()

    private fun boundsOf(root: ReaderActivity, file: File): Rect? {
        val name = ReaderActivity.pageTransitionName(file)
        fun find(view: View): Rect? {
            if (view is ReaderPageImageView && view.transitionName == name && view.isReady) {
                return Rect().takeIf(view::getGlobalVisibleRect)
            }
            if (view is ViewGroup) {
                for (index in 0 until view.childCount) find(view.getChildAt(index))?.let { return it }
            }
            return null
        }
        return find(root.window.decorView)
    }

    private fun await(
        scenario: ActivityScenario<ReaderActivity>,
        message: String,
        condition: (ReaderActivity) -> Boolean
    ) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        do {
            var ready = false
            scenario.onActivity { ready = condition(it) }
            if (ready) return
            SystemClock.sleep(50)
        } while (SystemClock.uptimeMillis() < deadline)
        throw AssertionError(message)
    }
}
