package com.flamyoad.tsukiviewer.core.utils

import java.io.File
import java.io.FileFilter
import java.util.*

val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

class ImageFileFilter : FileFilter {

    override fun accept(file: File?): Boolean {
        if (file == null) {
            return false
        }

        for (extension in imageExtensions) {
            if (file.name.lowercase(Locale.ROOT).endsWith(extension)) {
                return true
            }
        }
        return false
    }
}
