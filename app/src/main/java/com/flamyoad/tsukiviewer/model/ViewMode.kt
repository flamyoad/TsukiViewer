package com.flamyoad.tsukiviewer.model

enum class ViewMode(private val num: Int) {
    NORMAL_GRID(0),
    MINI_GRID(1),
    SCALED(2);

    fun toInt(): Int = num
}

