package com.flamyoad.tsukiviewer.core.repository

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.flamyoad.tsukiviewer.core.db.dao.CoverFingerprintDao
import com.flamyoad.tsukiviewer.core.model.CoverFingerprintEntry
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

private const val GRID_SIZE = 9
private const val MAX_DECODE_SIZE = CHARACTER_INPUT_SIZE
private const val MAX_MATCH_DISTANCE = 12

internal data class CoverFingerprint(val horizontal: Long, val vertical: Long, val palette: Long? = null) {
    fun distanceTo(other: CoverFingerprint): Int? {
        val edges = java.lang.Long.bitCount(horizontal xor other.horizontal) +
            java.lang.Long.bitCount(vertical xor other.vertical)
        val a = palette ?: return edges.takeIf { it <= MAX_MATCH_DISTANCE }
        val b = other.palette ?: return edges.takeIf { it <= MAX_MATCH_DISTANCE }
        var sharedColor = 0
        var colorA = 0
        var colorB = 0
        var sharedHues = 0
        for (hue in 0 until COLOR_HUES) {
            val weightA = paletteWeight(a, hue)
            val weightB = paletteWeight(b, hue)
            val shared = minOf(weightA, weightB)
            colorA += weightA
            colorB += weightB
            sharedColor += shared
            if (shared > 0) sharedHues++
        }

        // A colored cover needs some agreement in hue even when the edges align closely.
        if (edges <= MAX_MATCH_DISTANCE && (maxOf(colorA, colorB) < 3 || sharedColor >= 2)) return edges
        if (minOf(colorA, colorB) < 3 || sharedColor < 3) return null
        val overlap = sharedColor.toDouble() / maxOf(colorA, colorB)
        val edgeLimit = if (sharedHues >= 2) 64 else 36
        if (overlap < 0.68 || edges > edgeLimit) return null

        // Palette matches rank below near-duplicates, with hue overlap weighted above edges.
        return MAX_MATCH_DISTANCE + 1 + ((1 - overlap) * 64).roundToInt() + edges / 4
    }
}

private const val COLOR_HUES = 12

private fun paletteWeight(signature: Long, bin: Int): Int = (signature ushr (bin * 4) and 0xf).toInt()

internal fun coverPalette(pixels: IntArray): Long {
    val bins = IntArray(16)
    for (pixel in pixels) {
        val r = pixel ushr 16 and 0xff
        val g = pixel ushr 8 and 0xff
        val b = pixel and 0xff
        val high = maxOf(r, g, b)
        val delta = high - minOf(r, g, b)
        if (high < 42 || delta < 30 || delta * 4 < high) {
            bins[COLOR_HUES + high / 64]++
            continue
        }
        val hue = when (high) {
            r -> (g - b).toDouble() / delta
            g -> 2.0 + (b - r).toDouble() / delta
            else -> 4.0 + (r - g).toDouble() / delta
        }
        bins[((hue * 2).roundToInt() + COLOR_HUES) % COLOR_HUES]++
    }

    var signature = 0L
    for ((index, count) in bins.withIndex()) {
        val weight = (count * 15.0 / pixels.size).roundToInt()
        signature = signature or (weight.toLong() shl (index * 4))
    }
    return signature
}

internal data class CoverMatch(val reason: RelatedMangaReason, val distance: Double)

internal fun matchCover(
    artworkDistance: Int?,
    sourceCharacter: ByteArray?,
    candidateCharacter: ByteArray?
): CoverMatch? {
    if (artworkDistance != null && artworkDistance <= MAX_MATCH_DISTANCE) {
        return CoverMatch(RelatedMangaReason.SIMILAR_COVER, artworkDistance.toDouble())
    }
    val visualDistance = if (sourceCharacter != null && candidateCharacter != null) {
        characterDistance(sourceCharacter, candidateCharacter)
    } else null
    if (visualDistance != null) {
        return if (visualDistance <= MAX_CHARACTER_DISTANCE) {
            CoverMatch(RelatedMangaReason.SIMILAR_CHARACTER_ART, visualDistance)
        } else null
    }
    return artworkDistance?.let { CoverMatch(RelatedMangaReason.SIMILAR_COVER, it.toDouble()) }
}

private data class CoverSignature(val artwork: CoverFingerprint?, val character: ByteArray?) {
    fun match(other: CoverSignature): CoverMatch? =
        matchCover(artwork?.let { other.artwork?.distanceTo(it) }, character, other.character)
}

internal class CoverFingerprints(
    private val dao: CoverFingerprintDao,
    private val assets: AssetManager
) {
    suspend fun findMatches(
        source: File,
        sourceCover: File,
        candidates: List<RelatedMangaCandidate>,
        onProgress: suspend (Map<File, CoverMatch>) -> Unit
    ): Map<File, CoverMatch> = refreshLock.withLock {
        val saved = dao.getAll().associateBy { it.directoryPath }
        val updates = mutableListOf<CoverFingerprintEntry>()
        val context = currentCoroutineContext()
        val model = lazy { CharacterCoverModel.open(assets) }

        fun fingerprint(directory: File, cover: File): CoverSignature? {
            context.ensureActive()
            val modified = cover.lastModified()
            val length = cover.length()
            if (length == 0L) return null
            val entry = saved[directory.path]
            if (entry != null && entry.coverPath == cover.path &&
                entry.lastModified == modified && entry.byteCount == length &&
                entry.algorithmVersion == ALGORITHM_VERSION
            ) {
                val artwork = if (entry.horizontalHash != null && entry.verticalHash != null) {
                    CoverFingerprint(entry.horizontalHash, entry.verticalHash, entry.paletteHash)
                } else null
                return CoverSignature(artwork, entry.characterFingerprint)
            }

            val result = decode(cover) { model.value?.fingerprint(it) }
            if (cover.lastModified() != modified || cover.length() != length) return null
            updates.add(
                CoverFingerprintEntry(
                    directory.path, cover.path, modified, length, ALGORITHM_VERSION,
                    result?.artwork?.horizontal, result?.artwork?.vertical, result?.artwork?.palette,
                    result?.character
                )
            )
            return result
        }

        try {
            val matches = mutableMapOf<File, CoverMatch>()
            val sourceFingerprint = fingerprint(source, sourceCover)
            if (sourceFingerprint != null && (sourceFingerprint.artwork != null || sourceFingerprint.character != null)) {
                var publishedCount = 0
                val (indexed, remaining) = candidates.partition {
                    saved[it.directory.path]?.algorithmVersion == ALGORITHM_VERSION
                }
                for (candidate in indexed + remaining) {
                    fingerprint(candidate.directory, candidate.cover)?.let { fingerprint ->
                        sourceFingerprint.match(fingerprint)?.let { matches[candidate.cover] = it }
                    }
                    if (updates.size >= BATCH_SIZE) {
                        dao.upsert(updates)
                        updates.clear()
                        if (matches.size > publishedCount) {
                            onProgress(matches)
                            publishedCount = matches.size
                        }
                    }
                }
            }
            context.ensureActive()
            if (updates.isNotEmpty()) dao.upsert(updates)
            matches
        } finally {
            if (model.isInitialized()) model.value?.close()
        }
    }

    private companion object {
        const val ALGORITHM_VERSION = 3
        const val BATCH_SIZE = 8
        val refreshLock = Mutex()
    }

    private fun decode(cover: File, embedCharacter: (Bitmap) -> ByteArray?): CoverSignature? {
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
            val artwork = fingerprintCover(luminance)?.copy(palette = coverPalette(pixels))
            // Flat placeholders and simple geometric covers are outside the embedding model's domain.
            val tonalBuckets = luminance.fold(0) { seen, tone -> seen or (1 shl (tone / 16)) }
            val character = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Integer.bitCount(tonalBuckets) >= 5) {
                embedCharacter(bitmap)
            } else null
            CoverSignature(artwork, character)
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
