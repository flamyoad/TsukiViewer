package com.flamyoad.tsukiviewer

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.CoverFingerprintEntry
import com.flamyoad.tsukiviewer.core.model.RecentTab
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @get:Rule
    val currentSchemas = MigrationTestHelper(instrumentation, AppDatabase::class.java)

    @Suppress("DEPRECATION")
    @get:Rule
    val legacySchemas = MigrationTestHelper(
        instrumentation,
        "com.flamyoad.tsukiviewer.db.AppDatabase",
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migratesVersionFiveToTenWithoutLosingLibraryData() = runBlocking {
        val name = "migration-5-to-10"
        currentSchemas.createDatabase(name, 5).use { db ->
            db.execSQL("INSERT INTO included_path VALUES ('/library')")
            db.execSQL("INSERT INTO doujin_details VALUES (7, 123, 'A Manga', '', 'A Manga', '/library/A Manga', 'A Manga')")
            db.execSQL("INSERT INTO tags VALUES (8, 'character', 'A Character', '', 1)")
            db.execSQL("INSERT INTO doujin_tags VALUES (7, 8)")
            db.execSQL("INSERT INTO bookmark_group VALUES ('Favorites')")
            db.execSQL("INSERT INTO bookmark_item VALUES (9, '/library/A Manga', 'Favorites', 1)")
            db.execSQL("INSERT INTO recent_tabs VALUES (10, 'A Manga', '/library/A Manga', 'cover.png')")
        }

        currentSchemas.runMigrationsAndValidate(name, 10, true, AppDatabase.MIGRATION_5_6, AppDatabase.MIGRATION_6_7, AppDatabase.MIGRATION_7_8, AppDatabase.MIGRATION_8_9, AppDatabase.MIGRATION_9_10).use { db ->
            for ((table, expected) in mapOf(
                "included_path" to 1, "doujin_details" to 1, "tags" to 1, "doujin_tags" to 1,
                "bookmark_group" to 1, "bookmark_item" to 1, "recent_tabs" to 1, "cover_fingerprints" to 0,
                "related_manga_cache" to 0
            )) {
                db.query("SELECT COUNT(*) FROM `$table`").use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals(table, expected, cursor.getInt(0))
                }
            }
        }

        fun open() = Room.databaseBuilder(instrumentation.targetContext, AppDatabase::class.java, name)
            .addMigrations(AppDatabase.MIGRATION_5_6, AppDatabase.MIGRATION_6_7, AppDatabase.MIGRATION_7_8, AppDatabase.MIGRATION_8_9, AppDatabase.MIGRATION_9_10)
            .build()

        val entry = CoverFingerprintEntry("/library/Untagged", "/library/Untagged/1.png", 1, 50, 1, 12, -31)
        open().useDatabase { db ->
            val details = requireNotNull(db.doujinDetailsDao().getLongDetailsByPathBlocking("/library/A Manga"))
            assertEquals("A Manga", details.doujinDetails.fullTitleEnglish)
            assertEquals("A Character", details.tags.single().name)
            db.coverFingerprintDao().upsert(listOf(entry))
        }
        open().useDatabase { db ->
            assertEquals(listOf(entry), db.coverFingerprintDao().getAll())
            val replacement = entry.copy(
                coverPath = "/library/Untagged/2.png", lastModified = 2, horizontalHash = 22, paletteHash = 123L
            )
            db.coverFingerprintDao().upsert(listOf(replacement))
            assertEquals(listOf(replacement), db.coverFingerprintDao().getAll())
        }
    }

    @Test
    fun migratesVersionSixAndPreservesCoverFingerprints() = runBlocking {
        val name = "migration-6-to-8"
        currentSchemas.createDatabase(name, 6).use { db ->
            db.execSQL("INSERT INTO cover_fingerprints VALUES ('/library/Manga', '/library/Manga/1.png', 1, 50, 1, 12, -31)")
        }
        currentSchemas.runMigrationsAndValidate(name, 8, true, AppDatabase.MIGRATION_6_7, AppDatabase.MIGRATION_7_8).close()
        Room.databaseBuilder(instrumentation.targetContext, AppDatabase::class.java, name)
            .addMigrations(AppDatabase.MIGRATION_6_7, AppDatabase.MIGRATION_7_8, AppDatabase.MIGRATION_8_9, AppDatabase.MIGRATION_9_10).build().useDatabase { db ->
                val fingerprint = db.coverFingerprintDao().getAll().single()
                assertEquals(12L, fingerprint.horizontalHash)
                assertEquals(null, fingerprint.paletteHash)
                assertEquals(null, db.relatedMangaCacheDao().find("/library/Manga"))
            }
    }

    @Test
    fun upgradesVersionSevenWithoutRemovingSavedResults() {
        val name = "migration-7-to-8"
        currentSchemas.createDatabase(name, 7).use { db ->
            db.execSQL("INSERT INTO related_manga_cache VALUES ('/library/Manga', 'old-key', 1, '{}')")
        }
        currentSchemas.runMigrationsAndValidate(name, 8, true, AppDatabase.MIGRATION_7_8).use { db ->
            db.query("SELECT libraryKey FROM related_manga_cache").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("old-key", cursor.getString(0))
            }
        }
    }

    @Test
    fun preservesTabsAndDoesNotReuseClosedIds() = runBlocking {
        val name = "migration-8-to-9"
        currentSchemas.createDatabase(name, 8).use { db ->
            db.execSQL("INSERT INTO recent_tabs VALUES (10, 'Book', '/library/Book', '/library/Book/1.png')")
        }
        currentSchemas.runMigrationsAndValidate(name, 9, true, AppDatabase.MIGRATION_8_9).close()
        Room.databaseBuilder(instrumentation.targetContext, AppDatabase::class.java, name)
            .addMigrations(AppDatabase.MIGRATION_8_9, AppDatabase.MIGRATION_9_10)
            .build()
            .useDatabase { db ->
                val dao = db.recentTabDao()
                val original = dao.get(10)
                assertEquals("Book", original.title)
                assertEquals(1, dao.delete(original))
                val other = RecentTab(title = "Other", dirPath = File("/library/Other"), thumbnail = File("/library/Other/1.png"))
                val otherId = dao.open(other)
                assertTrue(otherId > 10)
                assertEquals(otherId, dao.open(other))
                assertEquals(10L, dao.restore(original))
            }
    }

    @Test
    fun migratesLegacyVersionsThroughEight() {
        for (version in 3..4) {
            val name = "migration-$version-to-8"
            legacySchemas.createDatabase(name, version).close()
            currentSchemas.runMigrationsAndValidate(
                name, 8, true,
                AppDatabase.MIGRATION_3_4, AppDatabase.MIGRATION_4_5, AppDatabase.MIGRATION_5_6,
                AppDatabase.MIGRATION_6_7, AppDatabase.MIGRATION_7_8
            ).close()
        }
    }
}

internal inline fun <T> AppDatabase.useDatabase(block: (AppDatabase) -> T): T {
    try {
        return block(this)
    } finally {
        close()
    }
}
