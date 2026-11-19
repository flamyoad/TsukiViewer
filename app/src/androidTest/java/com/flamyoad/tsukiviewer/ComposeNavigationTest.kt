package com.flamyoad.tsukiviewer

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.IncludedPath
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ComposeNavigationTest {
    @get:Rule
    val compose = createEmptyComposeRule()

    @Test
    fun tabStacksRestoreAndBackFromTabRootReturnsHome() {
        ActivityScenario.launch(ComposeMainActivity::class.java).use { scenario ->
            compose.onNodeWithText("Local Storage").assertIsDisplayed()
            for (tab in listOf("Collections", "Tags", "Bookmarks", "Settings")) {
                tab(tab).performClick().assertIsSelected()
            }

            compose.onNodeWithText("Included folders", ignoreCase = true).performClick()
            tab("Home").performClick().assertIsSelected()
            tab("Settings").performClick()
            scenario.recreate()
            compose.onNodeWithText("Included Folder").assertIsDisplayed()

            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            tab("Settings").assertIsSelected()
            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            tab("Home").assertIsSelected()
        }
    }

    @Test
    fun searchOpensReaderAndBackReturnsToThePageGrid() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        withLibrary(context) { title ->
            ActivityScenario.launch(ComposeMainActivity::class.java).use { scenario ->
                compose.onNodeWithContentDescription("Search").performClick()
                compose.onNodeWithTag("search-title").performTextInput(title)
                compose.onNodeWithTag("submit-search").performClick()
                compose.waitUntil(10_000) {
                    compose.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
                }
                compose.onNodeWithText(title).performClick()
                compose.waitUntil(10_000) {
                    compose.onAllNodesWithText("Images").fetchSemanticsNodes().isNotEmpty()
                }
                compose.onNodeWithText("Images").performClick()
                compose.waitUntil(10_000) {
                    compose.onAllNodesWithContentDescription("Page 1").fetchSemanticsNodes().isNotEmpty()
                }
                compose.onNodeWithContentDescription("Page 1").performClick()
                compose.waitUntil(10_000) {
                    compose.onAllNodesWithText("Page: 1 / 3").fetchSemanticsNodes().isNotEmpty()
                }
                compose.onNodeWithText("Page: 1 / 3").performClick()
                compose.onNodeWithContentDescription("Page 2 thumbnail").performClick()
                compose.waitUntil(10_000) {
                    compose.onAllNodesWithText("Page: 2 / 3").fetchSemanticsNodes().isNotEmpty()
                }
                scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                compose.onNodeWithText("Page: 2 / 3").assertIsDisplayed()
                compose.waitForIdle()
                scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                compose.onNodeWithText("Images").assertIsSelected()
                compose.onNodeWithContentDescription("Page 2").assertIsDisplayed()
            }
        }
    }

    private fun tab(label: String) = compose.onNodeWithTag("bottom-nav-$label")

    private fun withLibrary(context: Context, test: (String) -> Unit) {
        val db = AppDatabase.getInstance(context)
        val root = File(context.filesDir, "compose-navigation-${System.nanoTime()}").apply { mkdirs() }
        val folder = File(root, "Compose navigation book").apply { mkdirs() }
        val path = IncludedPath(root)
        val prefs = MyAppPreference.getInstance(context).prefs
        val oldReaderMode = prefs.getString(PreferenceKeys.DEFAULT_READER_MODE, null)
        val oldLanding = prefs.getString(PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR, null)
        try {
            prefs.edit()
                .putString(PreferenceKeys.DEFAULT_READER_MODE, "HorizontalSwipe")
                .putString(PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR, "DoujinDetails")
                .commit()
            Bitmap.createBitmap(48, 64, Bitmap.Config.ARGB_8888).let { bitmap ->
                repeat(3) { index ->
                    bitmap.eraseColor(0xFF223344.toInt() + index * 0x00110000)
                    File(folder, "${index + 1}.png").outputStream().use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                }
                bitmap.recycle()
            }
            runBlocking { db.includedFolderDao().insert(path) }
            test(folder.name)
        } finally {
            prefs.edit()
                .putString(PreferenceKeys.DEFAULT_READER_MODE, oldReaderMode)
                .putString(PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR, oldLanding)
                .commit()
            runBlocking(Dispatchers.IO) {
                db.includedFolderDao().delete(path)
                db.recentTabDao().getByPath(folder.absolutePath)?.let { db.recentTabDao().delete(it) }
                db.searchHistoryDao().getLatestItem()?.takeIf { it.title == folder.name }
                    ?.let { db.searchHistoryDao().delete(it) }
            }
            root.deleteRecursively()
        }
    }
}
