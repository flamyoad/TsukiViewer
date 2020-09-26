package com.flamyoad.tsukiviewer.ui.home.collection

import com.flamyoad.tsukiviewer.model.CollectionItem

interface ActionModeListener {
    fun startActionMode()
    fun onMultiSelectionClick(item: CollectionItem)
}