package com.flamyoad.tsukiviewer.model

import androidx.room.Ignore

data class DoujinCollection(
    val name: String,

    @Ignore
    val isHeader: Boolean = false
)