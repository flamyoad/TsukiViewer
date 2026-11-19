package com.flamyoad.tsukiviewer.ui

import android.app.Activity
import android.view.KeyEvent
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import com.flamyoad.tsukiviewer.ui.navigation.Screen
import com.flamyoad.tsukiviewer.ui.navigation.TsukiNavHost
import com.flamyoad.tsukiviewer.ui.theme.ColorPrimary
import com.flamyoad.tsukiviewer.ui.theme.SubLightTextColor
import com.flamyoad.tsukiviewer.ui.theme.TextWhite
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme
import com.flamyoad.tsukiviewer.ui.theme.WineRed

/**
 * Data class representing a bottom navigation item.
 */
private data class BottomNavItem(
    val destination: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * List of bottom navigation items.
 */
private val bottomNavItems = listOf(
    BottomNavItem(
        destination = Screen.Home,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        destination = Screen.Collections,
        label = "Collections",
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder
    ),
    BottomNavItem(
        destination = Screen.Tags,
        label = "Tags",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    ),
    BottomNavItem(
        destination = Screen.Bookmarks,
        label = "Bookmarks",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.Bookmark
    ),
    BottomNavItem(
        destination = Screen.Settings,
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

/**
 * Main entry point composable for the TsukiViewer app.
 * This can be called from the app module's Activity.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TsukiViewerApp(
    modifier: Modifier = Modifier,
    onRestartApp: () -> Unit = {},
    onQuitApp: () -> Unit = {},
    setReaderVolumeHandler: (((KeyEvent) -> Boolean)?) -> Unit = {},
    appLockEnabled: Boolean = false,
    onAppLockChange: ((Boolean) -> Unit)? = null,
    appLockTimeoutLabel: String? = null,
    onAppLockTimeoutClick: (() -> Unit)? = null
) {
    TsukiViewerTheme(darkTheme = false) {
        var selectedTab by rememberSaveable { mutableIntStateOf(0) }
        val tabStacks = bottomNavItems.map { item ->
            key(item.destination) { rememberNavBackStack(item.destination) }
        }
        val currentStack = tabStacks[selectedTab]
        val currentDestination = currentStack.last()
        val showBottomNav = when (currentDestination) {
            Screen.Search, is Screen.SearchResults, is Screen.CollectionDetails,
            is Screen.DoujinDetails, is Screen.DoujinReader, is Screen.RecentTabs,
            is Screen.CollectionEditor, is Screen.MetadataEditor, is Screen.FetchStatus -> false
            else -> true
        }

        val context = LocalContext.current
        val isReader = currentDestination is Screen.DoujinReader
        DisposableEffect(context, isReader) {
            val window = (context as? Activity)?.window
            val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
            val previousBehavior = controller?.systemBarsBehavior
            if (isReader && controller != null) {
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsetsCompat.Type.statusBars())
            }
            onDispose {
                if (isReader && controller != null) {
                    controller.show(WindowInsetsCompat.Type.statusBars())
                    if (previousBehavior != null) controller.systemBarsBehavior = previousBehavior
                }
            }
        }
        val readerStatusInsets = WindowInsets.statusBarsIgnoringVisibility
        var showQuit by rememberSaveable { mutableStateOf(false) }
        BackHandler(enabled = selectedTab == 0 && currentStack.size == 1) {
            if (MyAppPreference.getInstance(context).askBeforeQuit()) showQuit = true else onQuitApp()
        }
        if (showQuit) {
            AlertDialog(
                onDismissRequest = { showQuit = false },
                title = { Text("Quit") },
                text = { Text("Do you want to close this application?") },
                confirmButton = { TextButton(onClick = {
                    showQuit = false
                    onQuitApp()
                }) { Text("Yes") } },
                dismissButton = { TextButton(onClick = { showQuit = false }) { Text("Return") } }
            )
        }

        Scaffold(
            contentWindowInsets = if (isReader) WindowInsets.safeDrawing.union(readerStatusInsets)
                else WindowInsets.safeDrawing,
            containerColor = when {
                isReader -> Color.Transparent
                !showBottomNav -> Color.Black
                else -> MaterialTheme.colorScheme.background
            },
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar(
                        containerColor = ColorPrimary,  // Dark gray like toolbar
                        contentColor = TextWhite        // White content
                    ) {
                        bottomNavItems.forEachIndexed { index, item ->
                            val selected = currentDestination == item.destination

                            NavigationBarItem(
                                modifier = Modifier.testTag("bottom-nav-${item.label}"),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                selected = selected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = WineRed,           // Accent color for selected icon
                                    selectedTextColor = TextWhite,          // White text when selected
                                    indicatorColor = ColorPrimary,          // No visible indicator background
                                    unselectedIconColor = SubLightTextColor, // Light gray for unselected
                                    unselectedTextColor = SubLightTextColor  // Light gray text
                                ),
                                onClick = { selectedTab = index }
                            )
                        }
                    }
                }
            },
            modifier = modifier.fillMaxSize().drawBehind {
                if (isReader) {
                    drawRect(Color(0xFF242424))
                    drawRect(
                        Color(0xFF4E4C4C),
                        size = Size(size.width, readerStatusInsets.getTop(this).toFloat())
                    )
                }
            }
        ) { innerPadding ->
            TsukiNavHost(
                tabStacks = tabStacks,
                selectedTab = selectedTab,
                onSelectSettings = {
                    selectedTab = bottomNavItems.indexOfFirst { it.destination == Screen.Settings }
                },
                onBack = {
                    val activeStack = tabStacks[selectedTab]
                    if (activeStack.size > 1) activeStack.removeAt(activeStack.lastIndex)
                    else selectedTab = 0
                },
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                onRestartApp = onRestartApp,
                setReaderVolumeHandler = setReaderVolumeHandler,
                appLockEnabled = appLockEnabled,
                onAppLockChange = onAppLockChange,
                appLockTimeoutLabel = appLockTimeoutLabel,
                onAppLockTimeoutClick = onAppLockTimeoutClick
            )
        }
    }
}
