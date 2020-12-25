package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "collection")
@TypeConverters(FolderConverter::class)

data class Collection(
    @PrimaryKey val id: Long? = null,

    val name: String,
    val coverPhoto: File,

    // If value is true, use AND logic for filtering, otherwise use OR logic
    val mustHaveAllTitles: Boolean,
    val mustHaveAllIncludedTags: Boolean,
    val mustHaveAllExcludedTags: Boolean,

    val minNumPages: Int,
    val maxNumPages: Int
)
