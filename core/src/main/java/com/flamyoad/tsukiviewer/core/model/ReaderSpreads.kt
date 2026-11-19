package com.flamyoad.tsukiviewer.core.model

import android.content.SharedPreferences
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys

data class ReaderSpreadPreferences(
    val enabled: Boolean,
    val separateCover: Boolean,
    val rightToLeft: Boolean
) {
    fun useSpreads(widthDp: Int, heightDp: Int, explicitlySelected: Boolean = false): Boolean =
        explicitlySelected || (enabled && widthDp >= 720 && widthDp > heightDp)

    fun save(prefs: SharedPreferences) {
        prefs.edit()
            .putBoolean(ENABLED, enabled)
            .putBoolean(SEPARATE_COVER, separateCover)
            .putBoolean(PreferenceKeys.READER_RIGHT_TO_LEFT, rightToLeft)
            .apply()
    }

    companion object {
        private const val ENABLED = "pref_reader_two_page"
        private const val SEPARATE_COVER = "pref_reader_separate_cover"

        fun read(prefs: SharedPreferences) = ReaderSpreadPreferences(
            enabled = prefs.getBoolean(ENABLED, true),
            separateCover = prefs.getBoolean(SEPARATE_COVER, true),
            rightToLeft = prefs.getBoolean(PreferenceKeys.READER_RIGHT_TO_LEFT, false)
        )
    }
}

/** Maps physical pager positions to logical image indices. */
data class ReaderSpreads(
    val pageCount: Int,
    val twoPages: Boolean,
    val separateCover: Boolean,
    val rightToLeft: Boolean
) {
    private val pagesPerItem = if (twoPages) 2 else 1
    private val coverOffset = if (twoPages && separateCover && pageCount > 0) 1 else 0
    val itemCount = coverOffset + (pageCount - coverOffset + pagesPerItem - 1) / pagesPerItem

    fun itemForPage(page: Int): Int {
        val logical = if (page < coverOffset) 0 else coverOffset + (page - coverOffset) / pagesPerItem
        return physicalPosition(logical)
    }

    fun pagesForItem(item: Int): IntRange {
        val logical = physicalPosition(item)
        if (logical < coverOffset) return 0..0
        val first = coverOffset + (logical - coverOffset) * pagesPerItem
        return first until minOf(first + pagesPerItem, pageCount)
    }

    private fun physicalPosition(logical: Int) = if (rightToLeft) itemCount - logical - 1 else logical
}
