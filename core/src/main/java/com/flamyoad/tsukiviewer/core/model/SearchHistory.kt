package com.flamyoad.tsukiviewer.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val id: Int? = null,

    val title: String,
    val tags: String,
    val mustIncludeAllTags: Boolean = false
) {
    fun sameWith(other: SearchHistory): Boolean {
        return this.title == other.title &&
                this.tags == other.tags &&
                this.mustIncludeAllTags == other.mustIncludeAllTags
    }
}