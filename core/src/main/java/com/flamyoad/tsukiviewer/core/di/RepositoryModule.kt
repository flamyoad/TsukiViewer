package com.flamyoad.tsukiviewer.core.di

import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.core.repository.CollectionRepository
import com.flamyoad.tsukiviewer.core.repository.DoujinRepository
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository
import com.flamyoad.tsukiviewer.core.repository.TagRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideBookmarkRepository(impl: BookmarkRepository): BookmarkRepository = impl

    @Provides
    @Singleton
    fun provideCollectionRepository(impl: CollectionRepository): CollectionRepository = impl

    @Provides
    @Singleton
    fun provideDoujinRepository(impl: DoujinRepository): DoujinRepository = impl

    @Provides
    @Singleton
    fun provideMetadataRepository(impl: MetadataRepository): MetadataRepository = impl

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(impl: SearchHistoryRepository): SearchHistoryRepository = impl

    @Provides
    @Singleton
    fun provideTagRepository(impl: TagRepository): TagRepository = impl
}
