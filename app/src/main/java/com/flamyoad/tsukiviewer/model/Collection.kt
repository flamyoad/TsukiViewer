package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "collection")
@TypeConverters(FolderConverter::class)

data class Collection(
    @PrimaryKey val id: Int? = null,

    val title: String,
    val tags: String,
    val coverPhoto: File
)
