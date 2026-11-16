package com.flamyoad.tsukiviewer.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "related_manga_cache")
data class RelatedMangaCacheEntry(
    @PrimaryKey val directoryPath: String,
    val libraryKey: String,
    val savedAt: Long,
    val payload: String
)
