package com.flamyoad.tsukiviewer.ui.reader

/*
    Contains methods that are used to interact between the ReaderActivity and its underlying x-Fragments
 */
interface ReaderListener {
    fun toggleBottomSheet(visibility: Int)
    fun onPageChange(pageNum: Int)
}