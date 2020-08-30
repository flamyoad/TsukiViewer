package com.flamyoad.tsukiviewer.model

import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "included_folders",
    foreignKeys = [
        ForeignKey(entity = IncludedPath::class,
            parentColumns = ["dir"],
            childColumns = ["parentDir"],
            deferred = true,
            onDelete = ForeignKey.CASCADE)])

@TypeConverters(FolderConverter::class)
data class IncludedFolder(
    @PrimaryKey
    val dir: File,

    val parentDir: File,

    val lastName: String
)