package com.flamyoad.tsukiviewer.ui.doujinpage

import com.flamyoad.tsukiviewer.model.BookmarkGroup

interface CollectionDialogListener {
    fun onCollectionTicked(collection: BookmarkGroup)
    fun onCollectionUnticked(collection: BookmarkGroup)
    fun onAddCollectionClicked()
}