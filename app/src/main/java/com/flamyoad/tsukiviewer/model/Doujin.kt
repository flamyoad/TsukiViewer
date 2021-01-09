package com.flamyoad.tsukiviewer.model

import android.net.Uri
import androidx.core.net.toUri
import com.flamyoad.tsukiviewer.utils.imageExtensions
import java.io.File

data class Doujin(
    val pic: Uri,
    val title: String,
    val numberOfItems: Int,
    val lastModified: Long,
    val path: File
) : Comparable<Doujin> { //  Comparable<T> interface needs to be implemented to use fun <T> compareBy()

    /*
    https://kotlinlang.org/docs/reference/data-classes.html#properties-declared-in-the-class-body
    These 2 properties are moved out of primary constructor to prevent them from being
    evaluated in equals() and hashcode()

    Otherwise, duplicate items might overlap in search result because there are 2 sources of data (db, fileExplorer)
    */
    var parentDir: File = File("")
    var shortTitle: String = ""
    var isSelected: Boolean = false

    constructor(
        pic: Uri,
        title: String,
        numberOfItems: Int,
        lastModified: Long,
        path: File,
        parentDir: File
    ) : this(pic, title, numberOfItems, lastModified, path) {
        this.parentDir = parentDir
    }

    override fun compareTo(other: Doujin): Int {
        return shortTitle.compareTo(other.shortTitle)
    }

    companion object {
        // Untested
        fun fromFile(currentDir: File, parentDir: File): Doujin? {
            val fileList = currentDir.listFiles() ?: return null
            val imageList = fileList.filter { f -> f.extension in imageExtensions }

            if (imageList.isNotEmpty()) {

                val coverImage = imageList.first().toUri()
                val title = currentDir.name
                val numberOfImages = imageList.size
                val lastModified = currentDir.lastModified()

                val doujin = Doujin(
                    coverImage,
                    title,
                    numberOfImages,
                    lastModified,
                    currentDir,
                    parentDir
                )
                return doujin
            }
            return null
        }
    }
}