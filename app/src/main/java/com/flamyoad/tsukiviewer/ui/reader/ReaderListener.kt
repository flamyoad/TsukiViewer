package com.flamyoad.tsukiviewer.ui.reader

interface ReaderListener {
    fun toggleBottomSheet(visibility: Int)
    fun onPageChange(pageNum: Int)
}