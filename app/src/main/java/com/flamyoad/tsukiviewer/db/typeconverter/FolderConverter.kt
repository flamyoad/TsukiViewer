package com.flamyoad.tsukiviewer.db.typeconverter

import androidx.room.TypeConverter
import java.io.File

class FolderConverter {

    @TypeConverter
    fun toFolder(folderPath: String): File {
        return File(folderPath)
    }

    @TypeConverter
    fun toString(folder: File): String {
        return folder.toString()
    }
}