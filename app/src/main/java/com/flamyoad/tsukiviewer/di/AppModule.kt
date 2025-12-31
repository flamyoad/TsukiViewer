package com.flamyoad.tsukiviewer.di

import android.app.Application
import android.content.Context
import com.flamyoad.tsukiviewer.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: MyApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun provideContext(): Context = application.applicationContext
}
