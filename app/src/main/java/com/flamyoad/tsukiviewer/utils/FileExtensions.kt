package com.flamyoad.tsukiviewer.utils

import androidx.core.net.toUri
import com.flamyoad.tsukiviewer.model.Doujin
import java.io.File

fun File.toDoujin(): Doujin? {
    val imageList = this.listFiles(ImageFileFilter()) ?: return null

    val doujin = Doujin(
        pic = imageList.first().toUri(),
        title = this.name,
        path = this,
        lastModified = this.lastModified(),
        numberOfItems = imageList.size
    )
    return doujin
}