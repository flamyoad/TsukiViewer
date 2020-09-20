package com.flamyoad.tsukiviewer.db.typeconverter

import androidx.room.TypeConverter
import com.flamyoad.tsukiviewer.model.TagSortingMode

class TagSortingModeConverter {

    @TypeConverter
    fun toName(sortMode: TagSortingMode): String {
        return sortMode.toString()
    }

    @TypeConverter
    fun toType(typeName: String): TagSortingMode {
        return TagSortingMode.valueOf(typeName)
    }
}