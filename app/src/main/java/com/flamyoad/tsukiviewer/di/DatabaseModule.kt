package com.flamyoad.tsukiviewer.di

import android.content.Context
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.db.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideIncludedPathDao(database: AppDatabase): IncludedPathDao = 
        database.includedFolderDao()

    @Provides
    fun provideDoujinDetailsDao(database: AppDatabase): DoujinDetailsDao = 
        database.doujinDetailsDao()

    @Provides
    fun provideTagsDao(database: AppDatabase): TagDao = 
        database.tagsDao()

    @Provides
    fun provideDoujinTagDao(database: AppDatabase): DoujinTagsDao = 
        database.doujinTagDao()

    @Provides
    fun provideFolderDao(database: AppDatabase): IncludedFolderDao = 
        database.folderDao()

    @Provides
    fun provideBookmarkGroupDao(database: AppDatabase): BookmarkGroupDao = 
        database.bookmarkGroupDao()

    @Provides
    fun provideBookmarkItemDao(database: AppDatabase): BookmarkItemDao = 
        database.bookmarkItemDao()

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao = 
        database.searchHistoryDao()

    @Provides
    fun provideCollectionDao(database: AppDatabase): CollectionDao = 
        database.collectionDao()

    @Provides
    fun provideCollectionCriteriaDao(database: AppDatabase): CollectionCriteriaDao = 
        database.collectionCriteriaDao()

    @Provides
    fun provideCollectionDoujinDao(database: AppDatabase): CollectionDoujinDao = 
        database.collectionDoujinDao()

    @Provides
    fun provideRecentTabDao(database: AppDatabase): RecentTabDao = 
        database.recentTabDao()
}
