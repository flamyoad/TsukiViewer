package com.flamyoad.tsukiviewer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.os.SystemClock
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.ui.reader.ReaderMode
import com.flamyoad.tsukiviewer.ui.reader.ReaderPageImageView
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ReaderGifPlaybackTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test fun playsInHorizontalSwipe() = verifyReader(ReaderMode.HorizontalSwipe)
    @Test fun playsInVerticalSwipe() = verifyReader(ReaderMode.VerticalSwipe)
    @Test fun playsInVerticalStrip() = verifyReader(ReaderMode.VerticalStrip)

    private fun verifyReader(mode: ReaderMode) {
        val context = instrumentation.targetContext
        val preferences = MyAppPreference.getInstance(context)
        val previousMode = preferences.getDefaultReaderMode()
        val directory = File(context.cacheDir, "reader-gif-${System.nanoTime()}").apply { mkdirs() }
        val fixtures = InstrumentationRegistry.getArguments().getString("gifFixtures")?.let(::File)
        val first = File(directory, "01.gif")
        val second = File(directory, "02.GIF")
        val still = File(directory, "03.jpg")
        val twoFrame = File(directory, "04.gif")
        val fallback = File(directory, "06.gif")
        val downsampled = File(directory, "05.gif")

        try {
            if (fixtures == null) {
                val bytes = Base64.decode(TEST_GIF, Base64.DEFAULT)
                first.writeBytes(bytes)
                second.writeBytes(bytes)
            } else {
                File(fixtures, first.name).copyTo(first)
                File(fixtures, second.name).copyTo(second)
                File(fixtures, twoFrame.name).copyTo(twoFrame)
                File(fixtures, fallback.name).copyTo(fallback)
                File(fixtures, downsampled.name).copyTo(downsampled)
            }
            val bitmap = Bitmap.createBitmap(1800, 2400, Bitmap.Config.RGB_565)
            try {
                bitmap.eraseColor(Color.MAGENTA)
                still.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
            } finally {
                bitmap.recycle()
            }

            preferences.setDefaultReaderMode(mode)
            val intent = Intent(context, ReaderActivity::class.java)
                .putExtra(DoujinImagesAdapter.DIRECTORY_PATH, directory.absolutePath)
            ActivityScenario.launch<ReaderActivity>(intent).use { scenario ->
                assertAnimated(scenario, first)
                if (mode == ReaderMode.VerticalStrip) {
                    assertBothVisibleGifsAnimate(scenario, first, second)
                }
                scenario.onActivity { activity ->
                    val page = requireNotNull(findPage(activity.window.decorView, first))
                    val scale = page.scale
                    page.setScaleAndCenter(scale * 2, PointF(page.sWidth / 2f, page.sHeight / 2f))
                }
                assertAnimated(scenario, first)

                jumpToPage(scenario, 1)
                assertAnimated(scenario, second)
                scenario.moveToState(Lifecycle.State.STARTED)
                assertStill(scenario, second)
                scenario.moveToState(Lifecycle.State.RESUMED)
                assertAnimated(scenario, second)

                jumpToPage(scenario, 2)
                val stillFrame = awaitFrame(scenario, still)
                assertStill(scenario, still)
                assertEquals(stillFrame, awaitFrame(scenario, still))
                scenario.onActivity { activity ->
                    val photo = requireNotNull(findPage(activity.window.decorView, still))
                    assertEquals(1800, photo.sWidth)
                    assertEquals(2400, photo.sHeight)
                }

                if (downsampled.exists()) {
                    jumpToPage(scenario, 4)
                    assertAnimated(scenario, downsampled)
                }
                if (twoFrame.exists()) {
                    jumpToPage(scenario, 3)
                    assertAnimated(scenario, twoFrame, expectedFrames = 2)
                }
                if (fallback.exists()) {
                    jumpToPage(scenario, 5)
                    assertStill(scenario, fallback)
                }
                jumpToPage(scenario, 0)
                assertAnimated(scenario, first)
                scenario.recreate()
                assertAnimated(scenario, first)

                if (fixtures != null && mode == ReaderMode.HorizontalSwipe) {
                    repeat(6) {
                        jumpToPage(scenario, 1)
                        assertAnimated(scenario, second)
                        jumpToPage(scenario, 0)
                        assertAnimated(scenario, first)
                    }
                }
            }
        } finally {
            preferences.setDefaultReaderMode(previousMode)
            val dao = AppDatabase.getInstance(context).recentTabDao()
            runBlocking { dao.getByPath(directory.absolutePath)?.let(dao::delete) }
            directory.deleteRecursively()
        }
    }

    private fun jumpToPage(scenario: ActivityScenario<ReaderActivity>, position: Int) {
        scenario.onActivity { activity ->
            val sidebar = activity.findViewById<RecyclerView>(R.id.pageSidebar)
            if (sidebar.visibility != View.VISIBLE) {
                assertTrue(activity.findViewById<View>(R.id.txtCurrentPageNumber).performClick())
            }
            sidebar.scrollToPosition(position)
        }
        val deadline = SystemClock.uptimeMillis() + 5_000
        do {
            var clicked = false
            scenario.onActivity { activity ->
                val sidebar = activity.findViewById<RecyclerView>(R.id.pageSidebar)
                clicked = sidebar.findViewHolderForAdapterPosition(position)?.itemView?.performClick() == true
            }
            if (clicked) return
            SystemClock.sleep(50)
        } while (SystemClock.uptimeMillis() < deadline)
        throw AssertionError("Could not select page ${position + 1} in sidebar")
    }

    private fun assertBothVisibleGifsAnimate(
        scenario: ActivityScenario<ReaderActivity>,
        first: File,
        second: File
    ) {
        val firstFrames = mutableSetOf<Int>()
        val secondFrames = mutableSetOf<Int>()
        val deadline = SystemClock.uptimeMillis() + 5_000
        do {
            scenario.onActivity { activity ->
                val root = activity.window.decorView
                val bounds = Rect()
                assertTrue(requireNotNull(findPage(root, first)).getGlobalVisibleRect(bounds))
                assertTrue(requireNotNull(findPage(root, second)).getGlobalVisibleRect(bounds))
            }
            frame(scenario, first)?.let(firstFrames::add)
            frame(scenario, second)?.let(secondFrames::add)
            if (firstFrames.size >= 3 && secondFrames.size >= 3) return
            SystemClock.sleep(70)
        } while (SystemClock.uptimeMillis() < deadline)
        throw AssertionError("Both visible GIF pages should advance their frames together")
    }

    private fun assertAnimated(
        scenario: ActivityScenario<ReaderActivity>,
        file: File,
        expectedFrames: Int = 3
    ) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        val frames = mutableSetOf(awaitFrame(scenario, file))
        do {
            SystemClock.sleep(70)
            frame(scenario, file)?.let(frames::add)
            if (frames.size >= expectedFrames) return
        } while (SystemClock.uptimeMillis() < deadline)
        throw AssertionError("${file.name} did not advance its rendered frames")
    }

    private fun assertStill(scenario: ActivityScenario<ReaderActivity>, file: File) {
        val first = awaitFrame(scenario, file)
        repeat(8) {
            SystemClock.sleep(70)
            assertEquals("${file.name} should show a still frame", first, frame(scenario, file))
        }
    }

    private fun awaitFrame(scenario: ActivityScenario<ReaderActivity>, file: File): Int {
        val deadline = SystemClock.uptimeMillis() + 5_000
        do {
            frame(scenario, file)?.let { return it }
            SystemClock.sleep(50)
        } while (SystemClock.uptimeMillis() < deadline)
        throw AssertionError("${file.name} did not render")
    }

    private fun frame(scenario: ActivityScenario<ReaderActivity>, file: File): Int? {
        var frame: Int? = null
        scenario.onActivity { activity ->
            val page = findPage(activity.window.decorView, file)
            if (page?.isReady != true || page.width == 0 || page.height == 0) return@onActivity
            val sample = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
            try {
                val canvas = Canvas(sample)
                canvas.scale(32f / page.width, 32f / page.height)
                page.draw(canvas)
                val pixels = IntArray(32 * 32)
                sample.getPixels(pixels, 0, 32, 0, 0, 32, 32)
                frame = pixels.contentHashCode()
            } finally {
                sample.recycle()
            }
        }
        return frame
    }

    private fun findPage(root: View, file: File): ReaderPageImageView? {
        if (root is ReaderPageImageView && root.transitionName == ReaderActivity.pageTransitionName(file)) {
            return root
        }
        if (root is ViewGroup) {
            for (index in 0 until root.childCount) {
                findPage(root.getChildAt(index), file)?.let { return it }
            }
        }
        return null
    }

    private companion object {
        // Three 12×16 solid-color frames, 200 ms per frame.
        const val TEST_GIF = "R0lGODlhDAAQAIEAAP8AAAAAAAAAAAAAACH/C05FVFNDQVBFMi4wAwEAAAAh+QQAFAAAACwAAAAADAAQAAAIGQABCBxIsKDBgwgTKlzIsKHDhxAjSpwoMCAAIfkEARQAAQAsAAAAAAwAEACBAIAAAAAAAAAAAAAACBkAAQgcSLCgwYMIEypcyLChw4cQI0qcKDAgACH5BAEUAAEALAAAAAAMABAAgQAA/wAAAAAAAAAAAAgZAAEIHEiwoMGDCBMqXMiwocOHECNKnCgwIAA7"
    }
}
