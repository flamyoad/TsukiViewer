package com.flamyoad.tsukiviewer.ui.home.collection

import com.flamyoad.tsukiviewer.model.CollectionItem

interface ToggleHeaderListener {
    fun toggleHeader(header: CollectionItem, headerPosition: Int)
}