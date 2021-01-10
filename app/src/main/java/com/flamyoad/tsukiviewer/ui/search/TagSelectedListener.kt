package com.flamyoad.tsukiviewer.ui.search

import com.flamyoad.tsukiviewer.model.Tag

interface TagSelectedListener {
    fun onTagSelected(tag: Tag)
}