package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_criteria")
data class CollectionCriteria (
    @PrimaryKey
    val id: Long? = null,

    val collectionId: Long,
    val type: String,
    val value: String)

{
    companion object {
        const val TITLE = "title"
        const val INCLUDED_TAGS = "included_tags"
        const val EXCLUDED_TAGS = "excluded_tags"
        const val DIRECTORY = "directory"
    }
}