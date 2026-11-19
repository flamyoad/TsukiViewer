package com.flamyoad.tsukiviewer.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReaderSpreadsTest {
    @Test
    fun coverAndFinalPageCanStandAlone() {
        val layout = ReaderSpreads(6, twoPages = true, separateCover = true, rightToLeft = false)
        assertEquals(listOf(0..0, 1..2, 3..4, 5..5), (0 until layout.itemCount).map(layout::pagesForItem))
    }

    @Test
    fun rightToLeftReversesPagerPositionsWhilePreservingPageIndices() {
        for (count in 1..8) {
            for (cover in listOf(false, true)) {
                val layout = ReaderSpreads(count, twoPages = true, separateCover = cover, rightToLeft = true)
                for (page in 0 until count) {
                    assertTrue(page in layout.pagesForItem(layout.itemForPage(page)))
                }
                assertEquals((0 until count).toList(),
                    (layout.itemCount - 1 downTo 0).flatMap { layout.pagesForItem(it).toList() })
            }
        }
    }

    @Test
    fun automaticSpreadsRequireWideLandscapeButManualSelectionWorksOnPhones() {
        val options = ReaderSpreadPreferences(enabled = true, separateCover = true, rightToLeft = false)
        assertTrue(options.useSpreads(800, 600))
        assertFalse(options.useSpreads(699, 480))
        assertFalse(options.useSpreads(800, 1000))
        assertFalse(options.copy(enabled = false).useSpreads(800, 600))
        assertTrue(options.copy(enabled = false).useSpreads(800, 1000, explicitlySelected = true))
        assertTrue(options.useSpreads(360, 800, explicitlySelected = true))
    }
}
