package com.flamyoad.tsukiviewer

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.IncludedPath
import com.flamyoad.tsukiviewer.core.repository.RelatedMangaReason
import com.flamyoad.tsukiviewer.core.repository.RelatedMangaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.math.sqrt

@RunWith(AndroidJUnit4::class)
class CharacterCoverBenchmarkTest {
    // Stage the public reference artwork in the debug app's cache before running the benchmark.
    @Test
    fun scoresNamedCharactersThroughTheAndroidRepository() = runBlocking<Unit> {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fixtures = File(context.cacheDir, "ccip-benchmark")
        val artwork = linkedMapOf(
            "ch_img_miku" to "Sun Garden",
            "img_prod_mikuv4x" to "Paper Station",
            "img_prod_mikunt" to "Glass Lantern",
            "img_prod_mikuv2" to "Orbit House",
            "ch_img_rin" to "Quiet Skies",
            "ch_img_len" to "Clockwork Bridge",
            "ch_img_luka" to "Spring Tide",
            "ch_img_kaito" to "Mountain Cabin",
            "ch_img_meiko" to "After the Rain"
        )
        assumeTrue(artwork.keys.all { File(fixtures, "$it.png").isFile })
        val library = File(context.cacheDir, "ccip-library-${System.nanoTime()}").apply { mkdir() }
        val preferences = context.getSharedPreferences(library.name, Context.MODE_PRIVATE)
        try {
            val directories = artwork.mapValues { (image, name) ->
                File(library, name).apply { mkdir() }.canonicalFile.also { directory ->
                    File(fixtures, "$image.png").copyTo(File(directory, "1.png"))
                }
            }
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build().useDatabase { db ->
                db.includedFolderDao().insert(IncludedPath(library))
                val repository = RelatedMangaRepository(db, context)
                val source = directories.getValue("ch_img_miku")
                val start = SystemClock.elapsedRealtime()
                val results = repository.observe(source, preferences).first()
                val coldMs = SystemClock.elapsedRealtime() - start
                val warmStart = SystemClock.elapsedRealtime()
                assertEquals(results, repository.observe(source, preferences).first())
                val warmMs = SystemClock.elapsedRealtime() - warmStart
                assertEquals(artwork.keys.drop(1).take(3).map(directories::getValue).toSet(),
                    results.map { it.directory }.toSet())
                assertTrue(results.all { it.reason == RelatedMangaReason.SIMILAR_CHARACTER_ART })

                val fingerprints = db.coverFingerprintDao().getAll().associateBy { it.directoryPath }
                val embeddings = directories.mapValues { (_, directory) ->
                    requireNotNull(fingerprints.getValue(directory.path).characterFingerprint)
                }
                val mikuKeys = artwork.keys.take(4)
                val otherKeys = artwork.keys.drop(4)
                val same = mikuKeys.flatMapIndexed { index, left ->
                    mikuKeys.drop(index + 1).map { right -> score(embeddings.getValue(left), embeddings.getValue(right)) }
                }
                val different = artwork.keys.toList().flatMapIndexed { index, left ->
                    artwork.keys.drop(index + 1).filter { it in otherKeys }
                        .map { right -> score(embeddings.getValue(left), embeddings.getValue(right)) }
                }
                assertTrue(same.all { it <= 0.16 })
                assertTrue(different.all { it > 0.16 })
                Log.i("CharacterCoverBenchmark", "coldMs=$coldMs warmMs=$warmMs same=$same different=$different")
            }
        } finally {
            preferences.edit().clear().commit()
            library.deleteRecursively()
        }
    }

    private fun score(a: ByteArray, b: ByteArray): Double {
        val dot = a.indices.sumOf { a[it].toInt() * b[it] }
        val squaredA = a.sumOf { it.toInt() * it }
        val squaredB = b.sumOf { it.toInt() * it }
        return (1 - dot / sqrt(squaredA.toDouble() * squaredB)) / 2
    }
}
