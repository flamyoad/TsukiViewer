package com.flamyoad.tsukiviewer.model

import android.net.Uri
import java.io.File

data class Doujin(
    val pic: Uri,
    val title: String,
    val numberOfItems: Int,
    val lastModified: Long,
    val path: File,

    var shortTitle: String = "",
    var parentDir: File = File("")

): Comparable<Doujin> { // Comparable<T> interface needs to be implemented to use fun <T> compareBy()
    
    override fun compareTo(other: Doujin): Int {
        return shortTitle.compareTo(other.shortTitle)
    }
}