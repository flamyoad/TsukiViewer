package com.flamyoad.tsukiviewer.core.utils.extensions

import androidx.core.net.toUri
import com.flamyoad.tsukiviewer.core.model.Doujin
import com.flamyoad.tsukiviewer.core.utils.ImageFileFilter
import java.io.File

fun File.toDoujin(): Doujin? {
    val imageList = this.listFiles(ImageFileFilter()) ?: return null
    if (imageList.isEmpty()) return null

    val doujin = Doujin(
        pic = imageList.first().toUri(),
        title = this.name,
        path = this,
        lastModified = this.lastModified(),
        numberOfItems = imageList.size
    )
    return doujin
}
