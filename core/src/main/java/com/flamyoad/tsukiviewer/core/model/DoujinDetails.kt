package com.flamyoad.tsukiviewer.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter
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
) {
    companion object {
        fun getEmptyObject(dir: File): DoujinDetails {
            return DoujinDetails(
                id = null,
                nukeCode = -1,
                shortTitleEnglish = dir.name,
                fullTitleEnglish = dir.name,
                fullTitleJapanese = "",
                absolutePath = dir,
                folderName = dir.name
            )
        }
    }
}