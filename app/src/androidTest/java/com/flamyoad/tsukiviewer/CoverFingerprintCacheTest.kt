package com.flamyoad.tsukiviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.IncludedPath
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.core.repository.RELATED_MANGA_PAGE_SIZE
import com.flamyoad.tsukiviewer.core.repository.RelatedMangaReason
import com.flamyoad.tsukiviewer.core.repository.RelatedMangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor

@RunWith(AndroidJUnit4::class)
class CoverFingerprintCacheTest {
    @Test
    fun persistsAndRefreshesFingerprintsForUntaggedFolders() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val work = File.createTempFile("fingerprint-test-", "", context.cacheDir)
        check(work.delete() && work.mkdir())
        val dbPath = File(work, "fingerprints.db")
        val library = File(work, "library").apply { mkdir() }
        val source = File(library, "Moon Garden").apply { mkdir() }.canonicalFile
        val target = File(library, "Hidden Station").apply { mkdir() }.canonicalFile
        writeCover(File(source, "1.png"))
        val targetCover = File(target, "1.png")
        writeCover(targetCover)
        val prefs = context.getSharedPreferences(work.name, Context.MODE_PRIVATE)

        fun open() = Room.databaseBuilder(context, AppDatabase::class.java, dbPath.path).build()

        try {
            val original = open().useDatabase { db ->
                db.includedFolderDao().insert(IncludedPath(library))
                val recommendations = RelatedMangaRepository(db).observe(source, prefs)
                check(prefs.edit().putBoolean(PreferenceKeys.SHOW_RELATED_MANGA, false).commit())
                assertTrue(recommendations.first().isEmpty())
                assertTrue(db.coverFingerprintDao().getAll().isEmpty())

                check(prefs.edit().remove(PreferenceKeys.SHOW_RELATED_MANGA).commit())
                assertEquals(RelatedMangaReason.SIMILAR_COVER, recommendations.first().single().reason)
                db.coverFingerprintDao().getAll().single { it.directoryPath == target.path }.also {
                    assertTrue(it.paletteHash != null)
                    assertEquals(2, it.algorithmVersion)
                }
            }

            open().useDatabase { db ->
                val recommendations = RelatedMangaRepository(db).observe(source, prefs)
                assertEquals(target, recommendations.first().single().directory)
                assertEquals(original, db.coverFingerprintDao().getAll().single { it.directoryPath == target.path })

                writeCover(targetCover, inverted = true)
                check(targetCover.setLastModified(original.lastModified + 2000))
                assertTrue(recommendations.first().isEmpty())
                val refreshed = db.coverFingerprintDao().getAll().single { it.directoryPath == target.path }
                assertNotEquals(original.horizontalHash, refreshed.horizontalHash)

                val replacement = File(target, "0.png")
                writeCover(replacement)
                assertEquals(target, recommendations.first().single().directory)
                val updated = db.coverFingerprintDao().getAll().single { it.directoryPath == target.path }
                assertEquals(replacement.path, updated.coverPath)
                assertEquals(original.horizontalHash, updated.horizontalHash)

                replacement.writeText("Not a valid image")
                assertTrue(recommendations.first().isEmpty())
                val corrupt = db.coverFingerprintDao().getAll().single { it.directoryPath == target.path }
                assertNull(corrupt.horizontalHash)
                assertNull(corrupt.verticalHash)

                writeCover(replacement)
                assertEquals(target, recommendations.first().single().directory)

                check(replacement.delete() && targetCover.delete())
                assertTrue(recommendations.first().isEmpty())
            }
        } finally {
            prefs.edit().clear().commit()
            work.deleteRecursively()
        }
    }

    @Test
    fun showsLoadingAndEmptyThenDefersCoverSearchUntilRequested() = runBlocking {
        withTimeout(10_000) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val work = File.createTempFile("related-pages-", "", context.cacheDir)
            check(work.delete() && work.mkdir())
            val library = File(work, "library").apply { mkdir() }
            val source = File(library, "Moon Garden").apply { mkdir() }
            writeCover(File(source, "1.png"))
            val prefs = context.getSharedPreferences(work.name, Context.MODE_PRIVATE)
            try {
                val queries = ConcurrentLinkedQueue<String>()
                Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                    .setQueryCallback({ sql, _ -> queries.add(sql) }, Executor { it.run() })
                    .build().useDatabase { db ->
                    db.includedFolderDao().insert(IncludedPath(library))
                    val requested = MutableStateFlow(RELATED_MANGA_PAGE_SIZE)
                    val results = RelatedMangaRepository(db).observePages(source, prefs, requested)

                    check(prefs.edit().putBoolean(PreferenceKeys.SHOW_RELATED_MANGA, false).commit())
                    queries.clear()
                    assertNull(results.first())
                    assertTrue(queries.isEmpty())
                    check(prefs.edit().remove(PreferenceKeys.SHOW_RELATED_MANGA).commit())
                    val empty = results.take(2).toList()
                    assertTrue(empty[0]?.searching == true)
                    assertTrue(empty[1]?.searching == false && empty[1]?.matches.orEmpty().isEmpty())

                    for (volume in 1..8) {
                        val directory = File(library, "Moon Garden Vol. $volume").apply { mkdir() }
                        writeCover(File(directory, "1.png"))
                    }
                    val coverOnly = File(library, "Hidden Station").apply { mkdir() }
                    writeCover(File(coverOnly, "1.png"))
                    val cachedEmpty = results.first()
                    assertTrue(cachedEmpty?.searching == false && cachedEmpty.matches.isEmpty())
                    RelatedMangaRepository.invalidateLibrary(db)

                    val states = results.onEach { result ->
                        if (result?.canSearchMore == true) {
                            assertEquals(8, result.matches.size)
                            assertTrue(db.coverFingerprintDao().getAll().isEmpty())
                            requested.value += RELATED_MANGA_PAGE_SIZE
                        }
                    }.take(4).toList()

                    assertTrue(states[0]?.searching == true)
                    assertTrue(states[1]?.canSearchMore == true)
                    assertTrue(states[2]?.searching == true)
                    val complete = requireNotNull(states[3])
                    assertEquals(9, complete.matches.size)
                    assertEquals(RelatedMangaReason.SIMILAR_COVER, complete.matches.last().reason)

                    requested.value = RELATED_MANGA_PAGE_SIZE
                    queries.clear()
                    assertEquals(complete, results.first())
                    assertTrue(queries.isEmpty())

                    check(prefs.edit().putBoolean(PreferenceKeys.SHOW_RELATED_MANGA, false).commit())
                    RelatedMangaRepository.invalidateLibrary(db)
                    assertNull(results.first())
                    assertTrue(queries.isEmpty())
                    check(prefs.edit().remove(PreferenceKeys.SHOW_RELATED_MANGA).commit())
                    assertTrue(results.first()?.searching == true)
                }
            } finally {
                prefs.edit().clear().commit()
                work.deleteRecursively()
            }
        }
    }

    @Test
    fun reopensRankedResultsFromRoomAndReplacesInvalidEntries() = runBlocking {
        withTimeout(10_000) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val work = File.createTempFile("related-disk-cache-", "", context.cacheDir)
            check(work.delete() && work.mkdir())
            val library = File(work, "library").apply { mkdir() }
            val source = File(library, "Moon Garden").apply { mkdir() }.canonicalFile
            val candidate = File(library, "Moon Garden Vol. 2").apply { mkdir() }.canonicalFile
            writeCover(File(source, "1.png"))
            val cover = File(candidate, "1.png")
            writeCover(cover)
            val databasePath = File(work, "results.db").path
            val prefs = context.getSharedPreferences(work.name, Context.MODE_PRIVATE)
            val requested = MutableStateFlow(RELATED_MANGA_PAGE_SIZE)
            fun open() = Room.databaseBuilder(context, AppDatabase::class.java, databasePath).build()

            try {
                open().useDatabase { db ->
                    db.includedFolderDao().insert(IncludedPath(library))
                    val result = RelatedMangaRepository(db).observePages(source, prefs, requested)
                        .first { it?.searching == false }
                    assertEquals(candidate, result?.matches?.single()?.directory)
                    val saved = requireNotNull(db.relatedMangaCacheDao().find(source.path))
                    db.relatedMangaCacheDao().upsert(saved.copy(directoryPath = "/expired", savedAt = 1))
                }
                open().useDatabase { db ->
                    val first = RelatedMangaRepository(db).observePages(source, prefs, requested).first()
                    assertTrue(first?.searching == false)
                    assertEquals(candidate, first?.matches?.single()?.directory)
                    assertNull(db.relatedMangaCacheDao().find("/expired"))
                }
                check(cover.delete())
                open().useDatabase { db ->
                    val states = RelatedMangaRepository(db).observePages(source, prefs, requested).take(2).toList()
                    assertTrue(states.first()?.searching == true)
                    assertTrue(states.last()?.matches.orEmpty().isEmpty())
                    val saved = requireNotNull(db.relatedMangaCacheDao().find(source.path))
                    assertTrue(!saved.payload.contains(candidate.path))
                }
            } finally {
                prefs.edit().clear().commit()
                work.deleteRecursively()
            }
        }
    }

    private fun writeCover(file: File, inverted: Boolean = false) {
        val size = 90
        val pixels = IntArray(size * size) { index ->
            val dark = (index / size / 10 + index % size / 10) % 2 == 0
            if (dark xor inverted) Color.BLACK else Color.WHITE
        }
        val bitmap = Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
        try {
            file.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        } finally {
            bitmap.recycle()
        }
    }
}
