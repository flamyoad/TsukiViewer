package com.flamyoad.tsukiviewer.ui.di

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

/**
 * CompositionLocal for providing a ViewModelProvider.Factory from the app module.
 * This allows Compose screens in the ui module to use Dagger-injected ViewModels.
 */
val LocalViewModelFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("No ViewModelFactory provided! Make sure to provide LocalViewModelFactory in your Activity.")
}
