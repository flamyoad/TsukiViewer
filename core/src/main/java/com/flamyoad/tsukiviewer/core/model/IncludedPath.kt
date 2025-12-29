package com.flamyoad.tsukiviewer.core.model

import androidx.room.*
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "included_path")
@TypeConverters(FolderConverter::class)

data class IncludedPath(
    @PrimaryKey
    val dir: File
)