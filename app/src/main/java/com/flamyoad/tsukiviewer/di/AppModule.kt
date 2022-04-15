package com.flamyoad.tsukiviewer.di

import com.flamyoad.tsukiviewer.core.CoroutineDispatcherProvider
import com.flamyoad.tsukiviewer.core.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindDispatcherProvider(coroutineDispatcherProvider: CoroutineDispatcherProvider): DispatcherProvider
}