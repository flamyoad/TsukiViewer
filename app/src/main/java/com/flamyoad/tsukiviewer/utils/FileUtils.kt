package com.flamyoad.tsukiviewer.utils

import java.io.File

val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

object FileUtils {
    // Returns the first picture found in a folder
    fun getCoverImage(dir: File): File {
        val fileList = dir.listFiles() ?: return File("")

        val imageList = fileList.filter { f -> f.extension in imageExtensions }

        val coverImage = if (imageList.isNotEmpty()) {
            imageList.first()
        } else {
            File("")
        }

        return coverImage
    }

    // Returns the first picture found in a folder
    fun getCoverImage(path: String): File {
        val dir = File(path)

        val fileList = dir.listFiles() ?: return File("")

        val imageList = fileList.filter { f -> f.extension in imageExtensions }

        val coverImage = if (imageList.isNotEmpty()) {
            imageList.first()
        } else {
            File("")
        }
        return coverImage
    }
}