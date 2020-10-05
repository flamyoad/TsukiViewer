package com.flamyoad.tsukiviewer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    BookmarkItem::class
    ), version = 1)

abstract class AppDatabase: RoomDatabase() {

    abstract fun includedFolderDao(): IncludedPathDao
    abstract fun doujinDetailsDao(): DoujinDetailsDao
    abstract fun tagsDao(): TagDao
    abstract fun doujinTagDao(): DoujinTagsDao
    abstract fun folderDao(): IncludedFolderDao
    abstract fun bookmarkGroupDao(): BookmarkGroupDao
    abstract fun bookmarkItemDao(): BookmarkItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}