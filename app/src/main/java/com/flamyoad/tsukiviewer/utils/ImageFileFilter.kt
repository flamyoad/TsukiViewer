package com.flamyoad.tsukiviewer.utils

import java.io.File
import java.io.FileFilter
import java.util.*

class ImageFileFilter() : FileFilter {

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    override fun accept(f: File?): Boolean {
        if (f == null) {
            return false
        }

        for (extension in imageExtensions) {
            if (f.name.toLowerCase(Locale.ROOT).endsWith(extension)) {
                return true
            }
        }
        return false
    }

}