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

    val MIGRATION_3_4 = object: Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE collection")
            database.execSQL("CREATE TABLE IF NOT EXISTS `collection` (`id` INTEGER, `name` TEXT NOT NULL, `coverPhoto` TEXT NOT NULL, `mustHaveAllTitles` INTEGER NOT NULL, `mustHaveAllIncludedTags` INTEGER NOT NULL, `mustHaveAllExcludedTags` INTEGER NOT NULL, `minNumPages` INTEGER NOT NULL, `maxNumPages` INTEGER NOT NULL, PRIMARY KEY(`id`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `collection_criteria` (`id` INTEGER, `collectionId` INTEGER NOT NULL, `type` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`id`))");
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
    fun migrateFrom3To4() {
        testHelper.createDatabase(db, 3)

        testHelper.runMigrationsAndValidate(db, 4, true, MIGRATION_3_4)
    }
}