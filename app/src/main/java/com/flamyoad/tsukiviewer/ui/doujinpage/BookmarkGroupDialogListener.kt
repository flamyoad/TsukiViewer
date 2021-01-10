package com.flamyoad.tsukiviewer.ui.doujinpage

import com.flamyoad.tsukiviewer.model.BookmarkGroup

interface BookmarkGroupDialogListener {
    fun onBookmarkGroupTicked(collection: BookmarkGroup)
    fun onBookmarkGroupUnticked(collection: BookmarkGroup)
    fun onAddBookmarkGroup()
}