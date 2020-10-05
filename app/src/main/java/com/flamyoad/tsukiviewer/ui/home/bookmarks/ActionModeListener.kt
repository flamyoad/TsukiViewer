package com.flamyoad.tsukiviewer.ui.home.bookmarks

import com.flamyoad.tsukiviewer.model.BookmarkItem

interface ActionModeListener {
    fun startActionMode()
    fun onMultiSelectionClick(item: BookmarkItem)
}