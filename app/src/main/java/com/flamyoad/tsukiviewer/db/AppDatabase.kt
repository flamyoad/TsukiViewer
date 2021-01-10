package com.flamyoad.tsukiviewer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flamyoad.tsukiviewer.db.dao.*
import com.flamyoad.tsukiviewer.model.*
import com.flamyoad.tsukiviewer.model.Collection

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
    CollectionCriteria::class
    ), version = 4)

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

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance
                instance // return instance
            }
        }
    }
}