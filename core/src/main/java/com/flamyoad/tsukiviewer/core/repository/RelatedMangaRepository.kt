package com.flamyoad.tsukiviewer.core.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.LruCache
import androidx.annotation.StringRes
import com.flamyoad.tsukiviewer.core.R
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.core.model.Tag
import com.flamyoad.tsukiviewer.core.model.TagType
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.core.utils.imageExtensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException
import java.text.Normalizer
import java.util.Locale
import java.util.WeakHashMap

data class RelatedManga(
    val directory: File,
    val cover: File,
    val title: String,
    val reason: RelatedMangaReason
)

enum class RelatedMangaReason(@StringRes val label: Int) {
    SAME_SERIES_AND_CHARACTER(R.string.related_same_series_and_character),
    SAME_SERIES(R.string.related_same_series),
    SHARED_CHARACTER(R.string.related_shared_character),
    SIMILAR_TITLE(R.string.related_similar_title),
    SIMILAR_COVER(R.string.related_similar_cover),
    SIMILAR_CHARACTER_ART(R.string.related_similar_character_art),
    SHARED_FOLDER_TAG(R.string.related_shared_folder_tag),
    SHARED_ARTIST(R.string.related_shared_artist),
    SHARED_TAGS(R.string.related_shared_tags)
}

data class RelatedMangaResults(
    val matches: List<RelatedManga> = emptyList(),
    val searching: Boolean = false,
    val canSearchMore: Boolean = false
)

const val RELATED_MANGA_PAGE_SIZE = 6

class RelatedMangaRepository(private val database: AppDatabase, context: Context) {
    private val coverFingerprints = CoverFingerprints(database.coverFingerprintDao(), context.applicationContext.assets)

    private fun observeEnabled(preferences: SharedPreferences) = callbackFlow {
        fun sendCurrentSetting() {
            trySend(
                preferences.getBoolean(PreferenceKeys.SHOW_RELATED_MANGA, PreferenceKeys.SHOW_RELATED_MANGA_DEFAULT)
            )
        }
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == null || key == PreferenceKeys.SHOW_RELATED_MANGA) sendCurrentSetting()
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        sendCurrentSetting()
        awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(directory: File, preferences: SharedPreferences) = observeEnabled(preferences)
        .mapLatest { enabled -> if (enabled) findRelated(directory) else emptyList() }
        .flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observePages(directory: File, preferences: SharedPreferences, requestedCount: StateFlow<Int>) =
        observeEnabled(preferences).transformLatest<Boolean, RelatedMangaResults?> { enabled ->
            if (!enabled) {
                emit(null)
                return@transformLatest
            }
            val source = directory.canonicalOrNull()
            if (source == null) {
                emit(RelatedMangaResults())
                return@transformLatest
            }
            val owner = libraryCache(database)
            val cache = owner.current()
            val saved = cache.rankings.get(source)
            saved?.completed?.let {
                emit(RelatedMangaResults(it))
                return@transformLatest
            }
            val disk = cache.diskResults.get(source) ?: if (saved == null) {
                readSavedResults(owner, cache, source)
            } else null
            if (disk != null) {
                emit(disk)
                if (!disk.canSearchMore) return@transformLatest
                requestedCount.first { it > disk.matches.size }
            } else if (saved == null) {
                emit(RelatedMangaResults(searching = true))
            }

            val entry = saved ?: cache.prepare.withLock {
                cache.rankings.get(source) ?: run {
                    val library = cache.library ?: loadLibrary(cache.inputs?.library).also { cache.library = it }
                    val cancellation = currentCoroutineContext()
                    CachedRelatedRanking(
                        prepareRelatedManga(source, library.metadata[source], library.candidates, cancellation::ensureActive),
                        library.covers[source]
                    ).also { cache.rankings.put(source, it) }
                }
            }

            val ranking = entry.ranking
            if (ranking.hasWeakerCandidates && entry.completed == null) {
                val partial = RelatedMangaResults(ranking.strongMatches, canSearchMore = true)
                if (partial.matches.isNotEmpty()) writeSavedResults(owner, cache, source, entry.cover, partial)
                if (ranking.strongMatches.size >= requestedCount.value) {
                    emit(partial)
                    requestedCount.first { it > ranking.strongMatches.size }
                }
                if (entry.completed == null) {
                    val provisional = ranking.withCoverMatches(Int.MAX_VALUE, emptyMap())
                    emit(RelatedMangaResults(provisional, searching = true))
                }
            }

            val matches = entry.finish.withLock {
                entry.completed ?: run {
                    val results = if (ranking.hasWeakerCandidates) {
                        ranking.finish(Int.MAX_VALUE) { eligible ->
                            coverMatches(source, entry.cover, eligible) { partialMatches ->
                                emit(RelatedMangaResults(
                                    ranking.withCoverMatches(Int.MAX_VALUE, partialMatches),
                                    searching = true
                                ))
                            }
                        }
                    } else {
                        ranking.strongMatches
                    }
                    writeSavedResults(owner, cache, source, entry.cover, RelatedMangaResults(results))
                    entry.completed = results
                    results
                }
            }
            emit(RelatedMangaResults(matches))
        }.flowOn(Dispatchers.IO)

    companion object {
        fun invalidateLibrary(database: AppDatabase? = null) {
            synchronized(relatedLibraryCaches) {
                if (database == null) relatedLibraryCaches.values.forEach { it.invalidate() }
                else relatedLibraryCaches.getOrPut(database) { RelatedLibraryCache() }.invalidate()
            }
        }
    }

    private suspend fun findRelated(directory: File): List<RelatedManga> {
        val source = directory.canonicalOrNull() ?: return emptyList()
        val library = loadLibrary()
        val cancellation = currentCoroutineContext()
        return rankRelatedManga(
            source, library.metadata[source], library.candidates, cancellation::ensureActive
        ) { eligible -> coverMatches(source, library.covers[source], eligible) }
    }

    private suspend fun readSavedResults(
        owner: RelatedLibraryCache,
        cache: RelatedLibraryGeneration,
        source: File
    ): RelatedMangaResults? = owner.storage.withLock {
        cache.diskResults.get(source)?.let { return@withLock it }
        val key = initializeStorage(owner, cache) ?: return@withLock null
        val dao = database.relatedMangaCacheDao()
        val entry = dao.find(source.path) ?: return@withLock null
        val restored = if (entry.libraryKey == key &&
            entry.savedAt >= System.currentTimeMillis() - SAVED_RELATED_MANGA_TTL_MS
        ) entry.restore(source) else null
        if (restored == null) {
            dao.delete(entry.directoryPath, entry.savedAt, entry.payload)
        } else {
            cache.diskResults.put(source, restored)
        }
        restored
    }

    private suspend fun writeSavedResults(
        owner: RelatedLibraryCache,
        cache: RelatedLibraryGeneration,
        source: File,
        cover: File?,
        results: RelatedMangaResults
    ) = owner.storage.withLock {
        val previous = cache.diskResults.get(source)
        if (previous == results || previous != null && !previous.canSearchMore && results.canSearchMore) {
            return@withLock
        }
        val key = initializeStorage(owner, cache) ?: return@withLock
        val entry = saveRelatedManga(source, cover, key, results)
        currentCoroutineContext().ensureActive()
        if (owner.isCurrent(cache)) {
            database.relatedMangaCacheDao().upsert(entry)
            cache.diskResults.put(source, results)
        }
    }

    private suspend fun initializeStorage(owner: RelatedLibraryCache, cache: RelatedLibraryGeneration): String? {
        if (!owner.isCurrent(cache)) return null
        cache.inputs?.let { return it.key }
        val library = loadLibraryInputs()
        val key = relatedLibraryKey(library.roots, library.metadata.values)
        val dao = database.relatedMangaCacheDao()
        if (owner.mustClearDisk(cache)) {
            dao.clear()
            owner.markDiskCleared(cache)
        } else {
            dao.prune(key, System.currentTimeMillis() - SAVED_RELATED_MANGA_TTL_MS)
        }
        cache.inputs = SavedLibraryInputs(key, library)
        return key
    }

    private suspend fun loadLibraryInputs(): RelatedLibraryInputs {
        val metadata = database.doujinDetailsDao().getAllLongDetails()
            .associateBy { it.doujinDetails.absolutePath.canonicalOrNull() }
        val roots = database.includedFolderDao().getAllBlocking()
        return RelatedLibraryInputs(metadata, roots)
    }

    private suspend fun loadLibrary(inputs: RelatedLibraryInputs? = null): RelatedLibrary {
        val (metadata, roots) = inputs ?: loadLibraryInputs()
        val covers = discoverLibraryCovers(roots)
        return RelatedLibrary(metadata, covers, covers.map { (path, cover) ->
            RelatedMangaCandidate(path, cover, metadata[path])
        })
    }

    private suspend fun coverMatches(
        source: File,
        cover: File?,
        eligible: List<RelatedMangaCandidate>,
        onProgress: suspend (Map<File, CoverMatch>) -> Unit = {}
    ): Map<File, CoverMatch> = if (cover == null) emptyMap() else
        coverFingerprints.findMatches(source, cover, eligible, onProgress)
}

private data class RelatedLibraryInputs(
    val metadata: Map<File?, DoujinDetailsWithTags>,
    val roots: List<File>
)

private data class SavedLibraryInputs(val key: String, val library: RelatedLibraryInputs)

private data class RelatedLibrary(
    val metadata: Map<File?, DoujinDetailsWithTags>,
    val covers: Map<File, File>,
    val candidates: List<RelatedMangaCandidate>
)

private class CachedRelatedRanking(val ranking: RelatedMangaRanking, val cover: File?) {
    @Volatile var completed: List<RelatedManga>? = null
    val finish = Mutex()
}

private const val RELATED_LIBRARY_TTL_MS = 5 * 60 * 1000L

private class RelatedLibraryGeneration {
    val startedAt = SystemClock.elapsedRealtime()
    val prepare = Mutex()
    val rankings = LruCache<File, CachedRelatedRanking>(32)
    val diskResults = LruCache<File, RelatedMangaResults>(32)
    @Volatile var inputs: SavedLibraryInputs? = null
    var library: RelatedLibrary? = null
}

private class RelatedLibraryCache {
    val storage = Mutex()
    private var generation: RelatedLibraryGeneration? = null
    private var clearDisk = false

    @Synchronized
    fun current(): RelatedLibraryGeneration {
        val existing = generation
        if (existing != null && SystemClock.elapsedRealtime() - existing.startedAt < RELATED_LIBRARY_TTL_MS) {
            return existing
        }
        return RelatedLibraryGeneration().also { generation = it }
    }

    @Synchronized
    fun isCurrent(candidate: RelatedLibraryGeneration): Boolean = generation === candidate

    @Synchronized
    fun mustClearDisk(candidate: RelatedLibraryGeneration): Boolean = generation === candidate && clearDisk

    @Synchronized
    fun markDiskCleared(candidate: RelatedLibraryGeneration) {
        if (generation === candidate) clearDisk = false
    }

    @Synchronized
    fun invalidate() {
        generation = null
        clearDisk = true
    }
}

private val relatedLibraryCaches = WeakHashMap<AppDatabase, RelatedLibraryCache>()

private fun libraryCache(database: AppDatabase): RelatedLibraryCache = synchronized(relatedLibraryCaches) {
    relatedLibraryCaches.getOrPut(database) { RelatedLibraryCache() }
}

internal suspend fun discoverLibraryCovers(roots: List<File>): Map<File, File> {
    val covers = mutableMapOf<File, File>()
    val visited = mutableSetOf<File>()
    val context = currentCoroutineContext()
    val pageOrder = compareBy<File> { it.name.length }.then(naturalOrder())
    for (root in roots.mapNotNull { it.canonicalOrNull() }) {
        val pending = ArrayDeque<File>()
        pending.add(root)
        while (pending.isNotEmpty()) {
            context.ensureActive()
            val directory = pending.removeLast().canonicalOrNull() ?: continue
            if (!directory.startsWith(root) || !visited.add(directory)) continue
            val files = try {
                directory.listFiles()
            } catch (_: SecurityException) {
                null
            } ?: continue
            var cover: File? = null
            for (file in files) {
                context.ensureActive()
                if (file.isDirectory) {
                    pending.add(file)
                } else if (
                    file.extension.lowercase(Locale.ROOT) in imageExtensions &&
                    (cover == null || pageOrder.compare(file, cover) < 0) &&
                    file.isFile && file.canRead()
                ) {
                    cover = file
                }
            }
            if (cover != null) covers[directory] = cover
        }
    }
    return covers
}

private fun File.canonicalOrNull(): File? = try {
    canonicalFile
} catch (_: IOException) {
    null
} catch (_: SecurityException) {
    null
}

internal data class RelatedMangaCandidate(
    val directory: File,
    val cover: File,
    val metadata: DoujinDetailsWithTags?
)

internal suspend fun rankRelatedManga(
    source: File,
    sourceMetadata: DoujinDetailsWithTags?,
    candidates: List<RelatedMangaCandidate>,
    checkCancellation: () -> Unit = {},
    limit: Int = RELATED_MANGA_PAGE_SIZE,
    coverMatches: suspend (List<RelatedMangaCandidate>) -> Map<File, CoverMatch> = { emptyMap() }
): List<RelatedManga> {
    val ranking = prepareRelatedManga(source, sourceMetadata, candidates, checkCancellation)
    if (ranking.strongMatches.size >= limit || !ranking.hasWeakerCandidates) return ranking.strongMatches.take(limit)
    return ranking.finish(limit, coverMatches)
}

private fun prepareRelatedManga(
    source: File,
    sourceMetadata: DoujinDetailsWithTags?,
    candidates: List<RelatedMangaCandidate>,
    checkCancellation: () -> Unit
): RelatedMangaRanking {
    val sourceTitles = matchTitles(source.name, sourceMetadata)
    val library = candidates.filter { it.directory != source }
    val sourceTags = sourceMetadata?.tags.orEmpty().groupBy { it.type }
    val sourceFolderTags = folderTags(source.name)
    val sourceNames = sourceTags.values.flatten().folderTagNames()
    val generalTagType = TagType.Tags.getLowerCaseName()
    val libraryTagCounts = mutableMapOf<String, Int>()
    if (sourceTags[generalTagType].orEmpty().size >= 2) {
        for (candidate in library) {
            checkCancellation()
            candidate.metadata?.tags.orEmpty()
                .filter { it.type == generalTagType }
                .map { it.matchName() }.distinct()
                .forEach { libraryTagCounts[it] = (libraryTagCounts[it] ?: 0) + 1 }
        }
    }

    val weakerMatches = mutableListOf<Pair<RelatedMangaCandidate, Pair<RelatedManga, Double>?>>()
    val strongMatches = library.mapNotNull { candidate ->
        checkCancellation()
        val tags = candidate.metadata?.tags.orEmpty().groupBy { it.type }
        fun shared(type: TagType): Set<String> {
            val key = type.getLowerCaseName()
            return sourceTags[key].orEmpty().map { it.matchName() }.filter { it.isNotBlank() }
                .intersect(tags[key].orEmpty().map { it.matchName() }.toSet())
        }

        val series = shared(TagType.Parodies).size
        val characters = shared(TagType.Characters).size
        val artists = shared(TagType.Artists).size
        val titles = matchTitles(candidate.directory.name, candidate.metadata)
        val titleSimilarity = sourceTitles.maxOfOrNull { source ->
            titles.maxOfOrNull { title ->
                titleSimilarity(source, title)
            } ?: 0.0
        } ?: 0.0
        val candidateFolderTags = folderTags(candidate.directory.name)
        val sharedFolderTags = if (sourceFolderTags.isEmpty()) {
            candidateFolderTags.intersect(sourceNames)
        } else {
            sourceFolderTags.intersect(candidateFolderTags + tags.values.flatten().folderTagNames()) +
                candidateFolderTags.intersect(sourceNames)
        }
        val distinctiveTags = shared(TagType.Tags).filter { tag ->
            val count = libraryTagCounts[tag] ?: return@filter false
            count <= 2 || count.toLong() * 5 <= library.size
        }

        val score = when {
            series > 0 && characters > 0 ->
                RelatedMangaReason.SAME_SERIES_AND_CHARACTER to (series + characters).toDouble()
            series > 0 -> RelatedMangaReason.SAME_SERIES to series.toDouble()
            characters > 0 -> RelatedMangaReason.SHARED_CHARACTER to characters.toDouble()
            titleSimilarity >= 0.8 -> RelatedMangaReason.SIMILAR_TITLE to titleSimilarity
            sharedFolderTags.isNotEmpty() ->
                RelatedMangaReason.SHARED_FOLDER_TAG to sharedFolderTags.size.toDouble()
            artists > 0 -> RelatedMangaReason.SHARED_ARTIST to artists.toDouble()
            distinctiveTags.size >= 2 -> RelatedMangaReason.SHARED_TAGS to
                distinctiveTags.sumOf { 1.0 / libraryTagCounts.getValue(it) }
            else -> null
        }
        if (score == null) {
            weakerMatches.add(candidate to null)
            return@mapNotNull null
        }
        val (reason, relevance) = score
        val match = RelatedManga(candidate.directory, candidate.cover, candidate.displayTitle(), reason) to relevance
        if (reason >= RelatedMangaReason.SHARED_FOLDER_TAG) {
            weakerMatches.add(candidate to match)
            null
        } else {
            match
        }
    }
    return RelatedMangaRanking(strongMatches.sortedWith(relatedMatchOrder).map { it.first }, weakerMatches)
}

private val relatedMatchOrder = compareBy<Pair<RelatedManga, Double>> { it.first.reason }
    .thenByDescending { it.second }
    .thenBy { it.first.title.lowercase(Locale.ROOT) }
    .thenBy { it.first.directory.path }

private class RelatedMangaRanking(
    val strongMatches: List<RelatedManga>,
    private val weakerMatches: List<Pair<RelatedMangaCandidate, Pair<RelatedManga, Double>?>>
) {
    val hasWeakerCandidates: Boolean get() = weakerMatches.isNotEmpty()

    suspend fun finish(
        limit: Int,
        coverMatches: suspend (List<RelatedMangaCandidate>) -> Map<File, CoverMatch>
    ): List<RelatedManga> = withCoverMatches(limit, coverMatches(weakerMatches.map { it.first }))

    suspend fun withCoverMatches(limit: Int, covers: Map<File, CoverMatch>): List<RelatedManga> {
        val cancellation = currentCoroutineContext()
        val additional = weakerMatches.mapNotNull { (candidate, fallback) ->
            cancellation.ensureActive()
            val coverMatch = covers[candidate.cover] ?: return@mapNotNull fallback
            val title = fallback?.first?.title ?: candidate.displayTitle()
            RelatedManga(candidate.directory, candidate.cover, title, coverMatch.reason) to -coverMatch.distance
        }.sortedWith(relatedMatchOrder).take(limit - strongMatches.size).map { it.first }
        return strongMatches + additional
    }
}

private fun RelatedMangaCandidate.displayTitle(): String = metadata?.doujinDetails?.let {
    it.shortTitleEnglish.ifBlank { it.fullTitleEnglish }.ifBlank { it.fullTitleJapanese }
}.orEmpty().ifBlank { directory.name }

private fun Tag.matchName() = name.trim().lowercase(Locale.ROOT)

private val leadingReleaseMarkers = Regex("""^(?:\s*\[[^\]]+\])+\s*(?=\S)""")
private val trailingReleaseMarkers = Regex("""(?:\s*\[[^\]]+\])+$""")
private val volumeSuffix = Regex(
    """(?:[\s._-]+(?:vol(?:ume)?|ch(?:apter)?|part|book)\.?\s*#?\s*\d+(?:\.\d+)?|\s*第?\d+[巻話集])$"""
)
private val titlePunctuation = Regex("""[^\p{L}\p{M}\p{N}]+""")

private val bracketedFolderTag = Regex("""\[([^\[\]]+)]""")
private val folderTagPrefix = Regex("""^(?:author|artist|circle|group|series|parody|characters?|tags?)\s*[:=]\s*""")
private val numberedReleaseTag = Regex(
    """(?:c\s*\d+|comiket\s*\d+|(?:vol(?:ume)?|ch(?:apter)?|part|book|v)\s*\d+|\d+\s*(?:p|px|dpi|mb|pages?)?)"""
)
private val releaseWords = setOf(
    "en", "eng", "english", "jp", "jpn", "japanese", "日本語", "cn", "chinese", "中文",
    "kr", "korean", "de", "german", "es", "spanish", "fr", "french", "pt", "portuguese",
    "raw", "translated", "scanlated", "digital", "scan", "scanned", "release", "sample",
    "full", "complete", "color", "colour", "colorized", "textless", "uncensored", "censored",
    "remastered", "revised", "fixed", "high", "low", "quality", "hd", "sd", "web", "dl",
    "jpg", "jpeg", "png", "webp", "zip", "cbz", "pdf"
)

private fun folderTags(folder: String): Set<String> {
    val normalized = Normalizer.normalize(folder, Normalizer.Form.NFKC).lowercase(Locale.ROOT)
    return bracketedFolderTag.findAll(normalized)
        .map { it.groupValues[1].trim().replace(folderTagPrefix, "") }
        .filterNot { it.startsWith("translated by ") || it.startsWith("uploaded by ") }
        .map { it.replace(titlePunctuation, " ").trim() }
        .filter(::isFolderTagName)
        .toSet()
}

private val folderTagTypes = setOf("parody", "character", "artist", "group", "tag")

private fun List<Tag>.folderTagNames(): Set<String> = asSequence()
    .filter { it.type in folderTagTypes }
    .map { Normalizer.normalize(it.matchName(), Normalizer.Form.NFKC).replace(titlePunctuation, " ").trim() }
    .filter(::isFolderTagName)
    .toSet()

private fun isFolderTagName(name: String): Boolean =
    name.length >= 2 && name.any(Char::isLetter) &&
        !numberedReleaseTag.matches(name) && name.split(' ').any { it !in releaseWords }

private fun matchTitles(folder: String, metadata: DoujinDetailsWithTags?): Set<String> {
    val details = metadata?.doujinDetails
    return listOfNotNull(folder, details?.shortTitleEnglish, details?.fullTitleEnglish, details?.fullTitleJapanese)
        .map { raw ->
            val normalized = Normalizer.normalize(raw, Normalizer.Form.NFKC).lowercase(Locale.ROOT).trim()
            val title = normalized.replace(leadingReleaseMarkers, "").replace(trailingReleaseMarkers, "").trim()
                .ifBlank { normalized }
            title.replace(volumeSuffix, "").replace(titlePunctuation, " ").trim()
        }.filter { it.length >= 3 }.toSet()
}

private fun titleSimilarity(source: String, candidate: String): Double {
    if (source == candidate) return 1.0
    val a = source.split(' ').toSet()
    val b = candidate.split(' ').toSet()
    val common = a.intersect(b).size
    val wordSimilarity = if (common >= 2) 2.0 * common / (a.size + b.size) else 0.0

    // Character bigrams cover minor spelling differences and titles without word separators.
    if (minOf(source.length, candidate.length) < 6) return wordSimilarity
    val sourceBigrams = source.windowed(2).toSet()
    val candidateBigrams = candidate.windowed(2).toSet()
    val bigramSimilarity = 2.0 * sourceBigrams.intersect(candidateBigrams).size /
        (sourceBigrams.size + candidateBigrams.size)
    return maxOf(wordSimilarity, bigramSimilarity)
}
