package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "recent_tabs")
@TypeConverters(FolderConverter::class)

data class RecentTab(
    @PrimaryKey val id: Long? = null,
    val title: String,
    val dirPath: File,
    val thumbnail: File
)