package com.flamyoad.tsukiviewer.core.di

import android.app.Application
import android.content.Context
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.db.dao.*
import com.flamyoad.tsukiviewer.core.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        db: AppDatabase,
        groupDao: BookmarkGroupDao,
        itemDao: BookmarkItemDao
    ): BookmarkRepository {
        return BookmarkRepository(db, groupDao, itemDao)
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(
        db: AppDatabase,
        collectionDao: CollectionDao,
        criteriaDao: CollectionCriteriaDao,
        collectionDoujinDao: CollectionDoujinDao
    ): CollectionRepository {
        return CollectionRepository(db, collectionDao, criteriaDao, collectionDoujinDao)
    }

    @Provides
    @Singleton
    fun provideDoujinRepository(
        application: Application,
        doujinDetailsDao: DoujinDetailsDao,
        pathDao: IncludedPathDao
    ): DoujinRepository {
        return DoujinRepository(application, doujinDetailsDao, pathDao)
    }

    @Provides
    @Singleton
    fun provideMetadataRepository(
        context: Context,
        db: AppDatabase,
        pathDao: IncludedPathDao,
        doujinDetailsDao: DoujinDetailsDao,
        tagDao: TagDao,
        doujinTagDao: DoujinTagsDao,
        folderDao: IncludedFolderDao
    ): MetadataRepository {
        return MetadataRepository(context, db, pathDao, doujinDetailsDao, tagDao, doujinTagDao, folderDao)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(
        searchHistoryDao: SearchHistoryDao
    ): SearchHistoryRepository {
        return SearchHistoryRepository(searchHistoryDao)
    }

    @Provides
    @Singleton
    fun provideTagRepository(
        tagDao: TagDao
    ): TagRepository {
        return TagRepository(tagDao)
    }
}
