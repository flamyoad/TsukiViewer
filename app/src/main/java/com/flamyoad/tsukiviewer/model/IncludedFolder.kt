package com.flamyoad.tsukiviewer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "included_folders")
@TypeConverters(FolderConverter::class)

data class IncludedFolder(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,

    val dir: File
)