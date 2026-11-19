package com.flamyoad.tsukiviewer.ui.reader

interface ReaderListener {
    fun toggleBottomSheet(visibility: Int)
    fun onPageChange(pageNum: Int)
    fun onPageSpreadChange(anchor: Int, pages: IntRange) = onPageChange(anchor)
}