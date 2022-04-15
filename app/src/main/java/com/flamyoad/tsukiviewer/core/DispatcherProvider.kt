package com.flamyoad.tsukiviewer.core

import com.flamyoad.tsukiviewer.di.DefaultDispatcher
import com.flamyoad.tsukiviewer.di.IoDispatcher
import com.flamyoad.tsukiviewer.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

interface DispatcherProvider {
    fun io(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
}

class CoroutineDispatcherProvider @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher): DispatcherProvider {

    override fun io(): CoroutineDispatcher = ioDispatcher

    override fun ui(): CoroutineDispatcher = mainDispatcher

    override fun default(): CoroutineDispatcher = defaultDispatcher
}