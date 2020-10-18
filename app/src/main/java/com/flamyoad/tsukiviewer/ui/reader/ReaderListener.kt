package com.flamyoad.tsukiviewer.ui.reader

/*
    Contains methods that are used to interact between the ReaderActivity and its underlying fragments
 */
interface ReaderListener {
    fun toggleBottomSheet(visibility: Int)
    fun onPageChange(pageNum: Int)
}