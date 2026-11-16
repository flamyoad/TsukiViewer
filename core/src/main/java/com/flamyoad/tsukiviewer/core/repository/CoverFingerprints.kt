package com.flamyoad.tsukiviewer.core.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.flamyoad.tsukiviewer.core.db.dao.CoverFingerprintDao
import com.flamyoad.tsukiviewer.core.model.CoverFingerprintEntry
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.math.abs

private const val GRID_SIZE = 9
private const val MAX_DECODE_SIZE = 128
private const val MAX_MATCH_DISTANCE = 12

internal data class CoverFingerprint(val horizontal: Long, val vertical: Long) {
    fun distanceTo(other: CoverFingerprint): Int? {
        val distance = java.lang.Long.bitCount(horizontal xor other.horizontal) +
            java.lang.Long.bitCount(vertical xor other.vertical)
        return distance.takeIf { it <= MAX_MATCH_DISTANCE }
    }
}

internal class CoverFingerprints(private val dao: CoverFingerprintDao) {
    suspend fun findDistances(
        source: File,
        sourceCover: File,
        candidates: List<RelatedMangaCandidate>
    ): Map<File, Int> = refreshLock.withLock {
        val saved = dao.getAll().associateBy { it.directoryPath }
        val updates = mutableListOf<CoverFingerprintEntry>()
        val context = currentCoroutineContext()

        fun fingerprint(directory: File, cover: File): CoverFingerprint? {
            context.ensureActive()
            val modified = cover.lastModified()
            val length = cover.length()
            if (length == 0L) return null
            val entry = saved[directory.path]
            if (entry != null && entry.coverPath == cover.path &&
                entry.lastModified == modified && entry.byteCount == length &&
                entry.algorithmVersion == ALGORITHM_VERSION
            ) {
                return if (entry.horizontalHash != null && entry.verticalHash != null) {
                    CoverFingerprint(entry.horizontalHash, entry.verticalHash)
                } else null
            }

            val result = decode(cover)
            if (cover.lastModified() != modified || cover.length() != length) return null
            updates.add(
                CoverFingerprintEntry(
                    directory.path, cover.path, modified, length, ALGORITHM_VERSION,
                    result?.horizontal, result?.vertical
                )
            )
            return result
        }

        val sourceFingerprint = fingerprint(source, sourceCover)
        val distances = if (sourceFingerprint == null) emptyMap() else buildMap {
            for (candidate in candidates) {
                val fingerprint = fingerprint(candidate.directory, candidate.cover) ?: continue
                val distance = sourceFingerprint.distanceTo(fingerprint) ?: continue
                put(candidate.cover, distance)
            }
        }
        context.ensureActive()
        if (updates.isNotEmpty()) dao.upsert(updates)
        distances
    }

    private companion object {
        const val ALGORITHM_VERSION = 1
        val refreshLock = Mutex()
    }

    private fun decode(cover: File): CoverFingerprint? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(cover.path, bounds)
        if (bounds.outWidth < GRID_SIZE || bounds.outHeight < GRID_SIZE) return null

        val options = BitmapFactory.Options().apply {
            inSampleSize = Integer.highestOneBit(
                (maxOf(bounds.outWidth, bounds.outHeight) / MAX_DECODE_SIZE).coerceAtLeast(1)
            )
        }
        val bitmap = BitmapFactory.decodeFile(cover.path, options) ?: return null
        val thumbnail = Bitmap.createScaledBitmap(bitmap, GRID_SIZE, GRID_SIZE, true)
        return try {
            val pixels = IntArray(GRID_SIZE * GRID_SIZE)
            thumbnail.getPixels(pixels, 0, GRID_SIZE, 0, 0, GRID_SIZE, GRID_SIZE)
            val luminance = IntArray(pixels.size) { index ->
                val pixel = pixels[index]
                val red = pixel ushr 16 and 0xff
                val green = pixel ushr 8 and 0xff
                val blue = pixel and 0xff
                (299 * red + 587 * green + 114 * blue) / 1000
            }
            fingerprintCover(luminance)
        } finally {
            if (thumbnail !== bitmap) thumbnail.recycle()
            bitmap.recycle()
        }
    }
}

internal fun fingerprintCover(luminance: IntArray): CoverFingerprint? {
    var horizontal = 0L
    var vertical = 0L
    var distinctEdges = 0
    for (row in 0 until GRID_SIZE - 1) {
        for (column in 0 until GRID_SIZE - 1) {
            val index = row * GRID_SIZE + column
            val right = luminance[index] - luminance[index + 1]
            val down = luminance[index] - luminance[index + GRID_SIZE]
            horizontal = (horizontal shl 1) or if (right > 0) 1L else 0L
            vertical = (vertical shl 1) or if (down > 0) 1L else 0L
            if (abs(right) >= 16) distinctEdges++
            if (abs(down) >= 16) distinctEdges++
        }
    }
    // Low-detail covers such as blank placeholders provide too little evidence.
    if (distinctEdges < 20) return null
    return CoverFingerprint(horizontal, vertical)
}
