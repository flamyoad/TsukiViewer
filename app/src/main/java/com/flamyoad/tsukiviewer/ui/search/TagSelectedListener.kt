package com.flamyoad.tsukiviewer.ui.search

import com.flamyoad.tsukiviewer.core.model.Tag

interface TagSelectedListener {
    fun onTagSelected(tag: Tag)
}