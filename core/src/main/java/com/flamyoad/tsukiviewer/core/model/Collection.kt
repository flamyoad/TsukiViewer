package com.flamyoad.tsukiviewer.core.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "collection")
@TypeConverters(FolderConverter::class)

data class Collection(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,

    val name: String,
    val coverPhoto: File,

    // If value is true, use AND logic for filtering, otherwise use OR logic
    val mustHaveAllTitles: Boolean,
    val mustHaveAllIncludedTags: Boolean,
    val mustHaveAllExcludedTags: Boolean,

    val minNumPages: Int,
    val maxNumPages: Int
)
