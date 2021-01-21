package com.flamyoad.tsukiviewer.utils

import java.io.File
import java.io.FileFilter
import java.util.*

class ImageFileFilter() : FileFilter {

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    override fun accept(file: File?): Boolean {
        if (file == null) {
            return false
        }

        for (extension in imageExtensions) {
            if (file.name.toLowerCase(Locale.ROOT).endsWith(extension)) {
                return true
            }
        }
        return false
    }

}