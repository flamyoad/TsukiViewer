package com.flamyoad.tsukiviewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.flamyoad.tsukiviewer.ui.di.LocalViewModelFactory
import com.flamyoad.tsukiviewer.ui.navigation.TsukiNavHost
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * Main entry point composable for the TsukiViewer app.
 * This can be called from the app module's Activity.
 */
@Composable
fun TsukiViewerApp(
    viewModelFactory: ViewModelProvider.Factory,
    modifier: Modifier = Modifier,
    onRestartApp: () -> Unit = {}
) {
    CompositionLocalProvider(LocalViewModelFactory provides viewModelFactory) {
        TsukiViewerTheme {
            val navController = rememberNavController()
            
            TsukiNavHost(
                navController = navController,
                modifier = modifier.fillMaxSize(),
                onRestartApp = onRestartApp
            )
        }
    }
}
