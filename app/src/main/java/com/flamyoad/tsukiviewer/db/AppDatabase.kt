package com.flamyoad.tsukiviewer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flamyoad.tsukiviewer.db.dao.*
import com.flamyoad.tsukiviewer.model.*

const val DATABASE_NAME = "com.flamyoad.android.tsukiviewer.AppDatabase"

@Database(entities = arrayOf(
    IncludedPath::class,
    DoujinDetails::class,
    Tag::class,
    DoujinTag::class,
    IncludedFolder::class,
    BookmarkGroup::class,
    BookmarkItem::class,
    SearchHistory::class
    ), version = 2)

abstract class AppDatabase: RoomDatabase() {

    abstract fun includedFolderDao(): IncludedPathDao
    abstract fun doujinDetailsDao(): DoujinDetailsDao
    abstract fun tagsDao(): TagDao
    abstract fun doujinTagDao(): DoujinTagsDao
    abstract fun folderDao(): IncludedFolderDao
    abstract fun bookmarkGroupDao(): BookmarkGroupDao
    abstract fun bookmarkItemDao(): BookmarkItemDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`id` INTEGER, `title` TEXT NOT NULL, `tags` TEXT NOT NULL, `mustIncludeAllTags` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance // return instance
            }
        }
    }
}