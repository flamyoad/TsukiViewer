package com.flamyoad.tsukiviewer.core.model

data class EditorHistoryItem(
    val tag: Tag,
    val index: Int,
    val action: Mode
)

enum class Mode {
    ADD,
    REMOVE
}