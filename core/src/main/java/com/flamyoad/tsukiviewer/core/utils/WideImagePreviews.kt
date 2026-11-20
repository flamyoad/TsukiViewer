package com.flamyoad.tsukiviewer.core.utils

import android.graphics.BitmapFactory
import android.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun findWideImagePaths(paths: List<String>): Set<String> = withContext(Dispatchers.IO) {
    paths.filterTo(mutableSetOf()) { path ->
        ensureActive()
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@filterTo false

        val orientation = if (bounds.outMimeType == "image/jpeg") {
            try {
                ExifInterface(path).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )
            } catch (_: IOException) {
                ExifInterface.ORIENTATION_NORMAL
            }
        } else {
            ExifInterface.ORIENTATION_NORMAL
        }
        val rotated = orientation in ExifInterface.ORIENTATION_TRANSPOSE..ExifInterface.ORIENTATION_ROTATE_270
        val ratio = if (rotated) {
            bounds.outHeight.toFloat() / bounds.outWidth
        } else {
            bounds.outWidth.toFloat() / bounds.outHeight
        }
        ratio >= 1.4f
    }
}

fun widePreviewSpan(columns: Int, isWide: Boolean): Int = if (isWide && columns >= 3) 2 else 1
