package com.flamyoad.tsukiviewer

interface ActionModeListener<T> {
    fun startActionMode()
    fun onMultiSelectionClick(item: T)
}