package com.flamyoad.tsukiviewer.ui.doujinpage

import com.flamyoad.tsukiviewer.model.DoujinCollection

interface CollectionDialogListener {
    fun onCollectionTicked(collection: DoujinCollection)
    fun onCollectionUnticked(collection: DoujinCollection)
    fun onAddCollectionClicked()
}