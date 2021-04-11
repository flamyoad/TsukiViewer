package com.flamyoad.tsukiviewer

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.flamyoad.tsukiviewer.db.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {
    private val db = "test-database"

    private val MIGRATION_3_4 = object: Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE collection")
            database.execSQL("CREATE TABLE IF NOT EXISTS `collection` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `coverPhoto` TEXT NOT NULL, `mustHaveAllTitles` INTEGER NOT NULL, `mustHaveAllIncludedTags` INTEGER NOT NULL, `mustHaveAllExcludedTags` INTEGER NOT NULL, `minNumPages` INTEGER NOT NULL, `maxNumPages` INTEGER NOT NULL)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `collection_criteria` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `collectionId` INTEGER NOT NULL, `type` TEXT NOT NULL, `value` TEXT NOT NULL, `valueName` TEXT NOT NULL)");
        }
    }

    private val MIGRATION_4_5 = object: Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `recent_tabs` (`id` INTEGER, `title` TEXT NOT NULL, `dirPath` TEXT NOT NULL, `thumbnail` TEXT NOT NULL, PRIMARY KEY(`id`))");
        }
    }

    @get:Rule
    val testHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun testAllMigration() {
        testHelper.createDatabase(db, 3)
        testHelper.runMigrationsAndValidate(db, 4, true, MIGRATION_3_4)
        testHelper.runMigrationsAndValidate(db, 5, true, MIGRATION_4_5)
    }

    @Test
    @Throws(IOException::class)
    fun migrateFrom3To4() {
        testHelper.createDatabase(db, 3)
        testHelper.runMigrationsAndValidate(db, 4, true, MIGRATION_3_4)
    }

    @Test
    @Throws(IOException::class)
    fun migrateFrom4To5() {
        testHelper.createDatabase(db, 4)
        testHelper.runMigrationsAndValidate(db, 5, true, MIGRATION_4_5)
    }
}