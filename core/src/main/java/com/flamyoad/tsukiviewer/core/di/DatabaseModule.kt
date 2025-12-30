package com.flamyoad.tsukiviewer.core.di

import android.content.Context
import com.flamyoad.tsukiviewer.core.db.AppDatabase
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
    fun provideIncludedPathDao(database: AppDatabase) = database.includedFolderDao()

    @Provides
    fun provideDoujinDetailsDao(database: AppDatabase) = database.doujinDetailsDao()

    @Provides
    fun provideTagsDao(database: AppDatabase) = database.tagsDao()

    @Provides
    fun provideDoujinTagDao(database: AppDatabase) = database.doujinTagDao()

    @Provides
    fun provideFolderDao(database: AppDatabase) = database.folderDao()

    @Provides
    fun provideBookmarkGroupDao(database: AppDatabase) = database.bookmarkGroupDao()

    @Provides
    fun provideBookmarkItemDao(database: AppDatabase) = database.bookmarkItemDao()

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase) = database.searchHistoryDao()

    @Provides
    fun provideCollectionDao(database: AppDatabase) = database.collectionDao()

    @Provides
    fun provideCollectionCriteriaDao(database: AppDatabase) = database.collectionCriteriaDao()

    @Provides
    fun provideCollectionDoujinDao(database: AppDatabase) = database.collectionDoujinDao()

    @Provides
    fun provideRecentTabDao(database: AppDatabase) = database.recentTabDao()
}
