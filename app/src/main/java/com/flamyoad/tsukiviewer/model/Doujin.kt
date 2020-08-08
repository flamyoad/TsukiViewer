package com.flamyoad.tsukiviewer.model

import android.net.Uri
import java.io.File

data class Doujin(
    val pic: Uri,
    val title: String,
    val numberOfItems: Int,
    val lastModified: Long,
    val path: File
)