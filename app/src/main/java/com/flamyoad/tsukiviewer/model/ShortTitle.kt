package com.flamyoad.tsukiviewer.model

import androidx.room.ColumnInfo
import java.io.File

// Class containing 2 columns from @DoujinDetails. The purpose of this class is to contain the necessary
// columns needed for sorting by beautified name in LocalDoujinsFragment
data class ShortTitle(
    @ColumnInfo(name = "shortTitleEnglish") val shortTitleEnglish: String,
    @ColumnInfo(name = "absolutePath") val path: File
)