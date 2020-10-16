package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val id: Int? = null,

    val title: String,
    val tags: String,
    val mustIncludeAllTags: Boolean = false
)