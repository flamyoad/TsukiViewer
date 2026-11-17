package com.flamyoad.tsukiviewer.core.repository

import com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.core.model.RelatedMangaCacheEntry
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

internal const val SAVED_RELATED_MANGA_TTL_MS = 24 * 60 * 60 * 1000L
private const val RANKING_VERSION = 3

internal fun relatedLibraryKey(roots: List<File>, metadata: Collection<DoujinDetailsWithTags>): String {
    val digest = MessageDigest.getInstance("SHA-256")
    fun append(value: String) {
        val bytes = value.toByteArray(Charsets.UTF_8)
        digest.update(bytes.size.toString().toByteArray(Charsets.UTF_8))
        digest.update(0)
        digest.update(bytes)
    }

    append(RANKING_VERSION.toString())
    append(roots.size.toString())
    roots.sortedBy { it.path }.forEach {
        append(it.path)
        append(it.lastModified().toString())
    }
    append(metadata.size.toString())
    metadata.sortedBy { it.doujinDetails.absolutePath.path }.forEach {
        val details = it.doujinDetails
        append(details.absolutePath.path)
        append(details.shortTitleEnglish)
        append(details.fullTitleEnglish)
        append(details.fullTitleJapanese)
        append(it.tags.size.toString())
        it.tags.sortedWith(compareBy({ tag -> tag.type }, { tag -> tag.name })).forEach { tag ->
            append(tag.type)
            append(tag.name)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

internal fun saveRelatedManga(
    source: File,
    sourceCover: File?,
    libraryKey: String,
    results: RelatedMangaResults
): RelatedMangaCacheEntry {
    val matches = JSONArray()
    for (match in results.matches) {
        matches.put(JSONObject().apply {
            put("directory", match.directory.path)
            put("directoryModified", match.directory.lastModified())
            put("cover", fileStamp(match.cover))
            put("title", match.title)
            put("reason", match.reason.name)
        })
    }
    val payload = JSONObject().apply {
        put("sourceModified", source.lastModified())
        if (sourceCover != null) put("sourceCover", fileStamp(sourceCover))
        put("canSearchMore", results.canSearchMore)
        put("matches", matches)
    }
    return RelatedMangaCacheEntry(source.path, libraryKey, System.currentTimeMillis(), payload.toString())
}

internal fun RelatedMangaCacheEntry.restore(source: File): RelatedMangaResults? {
    try {
        val json = JSONObject(payload)
        if (!source.isDirectory || source.lastModified() != json.getLong("sourceModified")) return null
        if (json.has("sourceCover") && validFile(json.getJSONObject("sourceCover")) == null) return null

        val matches = json.getJSONArray("matches")
        return RelatedMangaResults(
            matches = List(matches.length()) { index ->
                val item = matches.getJSONObject(index)
                val directory = File(item.getString("directory"))
                if (!directory.isDirectory || directory.lastModified() != item.getLong("directoryModified")) return null
                RelatedManga(
                    directory,
                    validFile(item.getJSONObject("cover")) ?: return null,
                    item.getString("title"),
                    RelatedMangaReason.valueOf(item.getString("reason"))
                )
            },
            canSearchMore = json.getBoolean("canSearchMore")
        )
    } catch (_: JSONException) {
        return null
    } catch (_: IllegalArgumentException) {
        return null
    } catch (_: SecurityException) {
        return null
    }
}

private fun fileStamp(file: File) = JSONObject().apply {
    put("path", file.path)
    put("modified", file.lastModified())
    put("size", file.length())
}

private fun validFile(stamp: JSONObject): File? = File(stamp.getString("path")).takeIf {
    it.isFile && it.lastModified() == stamp.getLong("modified") && it.length() == stamp.getLong("size")
}
