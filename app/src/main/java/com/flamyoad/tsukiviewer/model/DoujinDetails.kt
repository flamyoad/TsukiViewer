package com.flamyoad.tsukiviewer.model

import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(tableName = "doujin_details")
@TypeConverters(FolderConverter::class)

data class DoujinDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val nukeCode: Int,

    val fullTitleEnglish: String,

    val fullTitleJapanese: String,

    val shortTitleEnglish: String,

    val absolutePath: File,

    val folderName: String
)