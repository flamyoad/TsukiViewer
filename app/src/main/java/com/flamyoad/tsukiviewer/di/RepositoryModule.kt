package com.flamyoad.tsukiviewer.di

import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.repository.BookmarkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideBookmarkRepository(db: AppDatabase): BookmarkRepository {
        return BookmarkRepository(db)
    }
}