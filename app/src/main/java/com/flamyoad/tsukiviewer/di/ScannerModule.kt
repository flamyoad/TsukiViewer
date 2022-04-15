package com.flamyoad.tsukiviewer.di

import com.flamyoad.tsukiviewer.repository.DirectoryManager
import com.flamyoad.tsukiviewer.repository.DirectoryManagerImpl
import com.flamyoad.tsukiviewer.repository.LocalStorageScannerService
import com.flamyoad.tsukiviewer.repository.LocalStorageScannerServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ScannerModule {
    @Binds
    fun bindLocalStorageScannerService(impl: LocalStorageScannerServiceImpl): LocalStorageScannerService

    @Binds
    fun bindDirectoryManager(impl: DirectoryManagerImpl): DirectoryManager
}