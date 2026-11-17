package com.flamyoad.tsukiviewer.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flamyoad.tsukiviewer.core.db.dao.*
import com.flamyoad.tsukiviewer.core.model.*
import com.flamyoad.tsukiviewer.core.model.Collection

const val DATABASE_NAME = "com.flamyoad.android.tsukiviewer.AppDatabase"

@Database(entities = arrayOf(
    IncludedPath::class,
    DoujinDetails::class,
    Tag::class,
    DoujinTag::class,
    IncludedFolder::class,
    BookmarkGroup::class,
    BookmarkItem::class,
    SearchHistory::class,
    Collection::class,
    CollectionCriteria::class,
    RecentTab::class,
    CoverFingerprintEntry::class,
    RelatedMangaCacheEntry::class
    ), version = 9)

abstract class AppDatabase: RoomDatabase() {

    abstract fun includedFolderDao(): IncludedPathDao
    abstract fun doujinDetailsDao(): DoujinDetailsDao
    abstract fun tagsDao(): TagDao
    abstract fun doujinTagDao(): DoujinTagsDao
    abstract fun folderDao(): IncludedFolderDao
    abstract fun bookmarkGroupDao(): BookmarkGroupDao
    abstract fun bookmarkItemDao(): BookmarkItemDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun collectionDao(): CollectionDao
    abstract fun collectionCriteriaDao(): CollectionCriteriaDao
    abstract fun collectionDoujinDao(): CollectionDoujinDao
    abstract fun recentTabDao(): RecentTabDao
    abstract fun coverFingerprintDao(): CoverFingerprintDao
    abstract fun relatedMangaCacheDao(): RelatedMangaCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`id` INTEGER, `title` TEXT NOT NULL, `tags` TEXT NOT NULL, `mustIncludeAllTags` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        val MIGRATION_2_3 = object: Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `collection` (`id` INTEGER, `title` TEXT NOT NULL, `tags` TEXT NOT NULL, `coverPhoto` TEXT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        val MIGRATION_3_4 = object: Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE `collection`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `collection` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `coverPhoto` TEXT NOT NULL, `mustHaveAllTitles` INTEGER NOT NULL, `mustHaveAllIncludedTags` INTEGER NOT NULL, `mustHaveAllExcludedTags` INTEGER NOT NULL, `minNumPages` INTEGER NOT NULL, `maxNumPages` INTEGER NOT NULL)");
                database.execSQL("CREATE TABLE IF NOT EXISTS `collection_criteria` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `collectionId` INTEGER NOT NULL, `type` TEXT NOT NULL, `value` TEXT NOT NULL, `valueName` TEXT NOT NULL)");
            }
        }

        val MIGRATION_4_5 = object: Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `recent_tabs` (`id` INTEGER, `title` TEXT NOT NULL, `dirPath` TEXT NOT NULL, `thumbnail` TEXT NOT NULL, PRIMARY KEY(`id`))");
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `cover_fingerprints` (
                        `directoryPath` TEXT NOT NULL PRIMARY KEY,
                        `coverPath` TEXT NOT NULL,
                        `lastModified` INTEGER NOT NULL,
                        `byteCount` INTEGER NOT NULL,
                        `algorithmVersion` INTEGER NOT NULL,
                        `horizontalHash` INTEGER,
                        `verticalHash` INTEGER
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `related_manga_cache` (
                        `directoryPath` TEXT NOT NULL PRIMARY KEY,
                        `libraryKey` TEXT NOT NULL,
                        `savedAt` INTEGER NOT NULL,
                        `payload` TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `cover_fingerprints` ADD COLUMN `paletteHash` INTEGER")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE `recent_tabs_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                        `title` TEXT NOT NULL,
                        `dirPath` TEXT NOT NULL,
                        `thumbnail` TEXT NOT NULL
                    )
                """.trimIndent())
                database.execSQL("""
                    INSERT INTO `recent_tabs_new` (`id`, `title`, `dirPath`, `thumbnail`)
                    SELECT `id`, `title`, `dirPath`, `thumbnail` FROM `recent_tabs`
                """.trimIndent())
                database.execSQL("DROP TABLE `recent_tabs`")
                database.execSQL("ALTER TABLE `recent_tabs_new` RENAME TO `recent_tabs`")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    .build()

                INSTANCE = instance
                instance // return instance
            }
        }
    }
}