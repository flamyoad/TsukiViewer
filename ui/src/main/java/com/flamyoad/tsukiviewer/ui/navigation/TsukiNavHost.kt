package com.flamyoad.tsukiviewer.ui.navigation

import android.view.KeyEvent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.flamyoad.tsukiviewer.ui.screens.bookmarks.BookmarksScreen
import com.flamyoad.tsukiviewer.ui.screens.collections.CollectionDetailsScreen
import com.flamyoad.tsukiviewer.ui.screens.collections.CollectionsScreen
import com.flamyoad.tsukiviewer.ui.screens.collections.CollectionEditorScreen
import com.flamyoad.tsukiviewer.ui.screens.doujin.DoujinDetailsScreen
import com.flamyoad.tsukiviewer.ui.screens.doujin.DoujinReaderScreen
import com.flamyoad.tsukiviewer.ui.screens.doujin.RecentTabsScreen
import com.flamyoad.tsukiviewer.ui.screens.doujin.FetchStatusScreen
import com.flamyoad.tsukiviewer.ui.screens.doujin.MetadataEditorScreen
import com.flamyoad.tsukiviewer.ui.screens.home.HomeScreen
import com.flamyoad.tsukiviewer.ui.screens.search.SearchScreen
import com.flamyoad.tsukiviewer.ui.screens.search.SearchResultsScreen
import com.flamyoad.tsukiviewer.ui.screens.settings.SettingsScreen
import com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders.IncludedFoldersScreen
import com.flamyoad.tsukiviewer.ui.screens.tags.TagDetailsScreen
import com.flamyoad.tsukiviewer.ui.screens.tags.TagsScreen
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun TsukiNavHost(
    tabStacks: List<MutableList<NavKey>>,
    selectedTab: Int,
    onSelectSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onRestartApp: () -> Unit,
    setReaderVolumeHandler: (((KeyEvent) -> Boolean)?) -> Unit,
    appLockEnabled: Boolean,
    onAppLockChange: ((Boolean) -> Unit)?,
    appLockTimeoutLabel: String?,
    onAppLockTimeoutClick: (() -> Unit)?
) {
    var readerReturn by remember { mutableStateOf<ReaderReturnAnchor?>(null) }
    val selectSettings by rememberUpdatedState(onSelectSettings)
    val navigateBack by rememberUpdatedState(onBack)
    val restartApp by rememberUpdatedState(onRestartApp)
    val volumeHandler by rememberUpdatedState(setReaderVolumeHandler)
    val lockEnabled by rememberUpdatedState(appLockEnabled)
    val lockChange by rememberUpdatedState(onAppLockChange)
    val lockTimeoutLabel by rememberUpdatedState(appLockTimeoutLabel)
    val lockTimeoutClick by rememberUpdatedState(onAppLockTimeoutClick)

    val tabEntries = tabStacks.map { stack ->
        val openDetails: (String) -> Unit = { path ->
            val current = stack.lastOrNull() as? Screen.DoujinDetails
            if (current?.path != path) stack.add(Screen.DoujinDetails(path))
        }
        val openReader: (String, Int, String?) -> Unit = { path, page, galleryEntry ->
            val destination = Screen.DoujinReader(path, page, galleryEntry)
            if (galleryEntry == null) readerReturn = null
            if (stack.lastOrNull() != destination) stack.add(destination)
        }
        val editMetadata: (List<String>) -> Unit = { paths ->
            val destination = Screen.MetadataEditor(paths.distinct())
            if (paths.isNotEmpty() && stack.lastOrNull() != destination) stack.add(destination)
        }
        val fetchMetadata: (List<String>) -> Unit = { paths ->
            val destination = Screen.FetchStatus(paths.distinct())
            if (paths.isNotEmpty() && stack.lastOrNull() != destination) stack.add(destination)
        }
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Screen.Home> {
                    HomeScreen(
                        onDoujinClick = openDetails,
                        onSearchClick = { if (stack.lastOrNull() != Screen.Search) stack.add(Screen.Search) },
                        onNavigateToSettings = { selectSettings() },
                        onEditMetadata = editMetadata,
                        onFetchMetadata = fetchMetadata,
                        onRecentTabs = {
                            if (stack.lastOrNull() !is Screen.RecentTabs) stack.add(Screen.RecentTabs())
                        }
                    )
                }
                entry<Screen.Search> {
                    SearchScreen { search ->
                        val destination = Screen.SearchResults(search.title, search.tags, search.mustIncludeAllTags)
                        if (stack.lastOrNull() != destination) stack.add(destination)
                    }
                }
                entry<Screen.SearchResults> { destination ->
                    SearchResultsScreen(
                        query = destination,
                        onDoujinClick = openDetails,
                        onNavigateBack = { navigateBack() }
                    )
                }
                entry<Screen.Collections> {
                    CollectionsScreen(
                        onCollectionClick = { id ->
                            val destination = Screen.CollectionDetails(id)
                            if (stack.lastOrNull() != destination) stack.add(destination)
                        },
                        onEditCollection = { id ->
                            val destination = Screen.CollectionEditor(id)
                            if (stack.lastOrNull() != destination) stack.add(destination)
                        }
                    )
                }
                entry<Screen.CollectionEditor> { destination ->
                    CollectionEditorScreen(destination.collectionId, onBack = { navigateBack() })
                }
                entry<Screen.Tags> {
                    TagsScreen(onTagClick = { id ->
                        val destination = Screen.TagDetails(id)
                        if (stack.lastOrNull() != destination) stack.add(destination)
                    })
                }
                entry<Screen.Bookmarks> { BookmarksScreen(onDoujinClick = openDetails) }
                entry<Screen.Settings> {
                    SettingsScreen(
                        onNavigateBack = { navigateBack() },
                        onNavigateToIncludedFolders = {
                            if (stack.lastOrNull() != Screen.IncludedFolders) stack.add(Screen.IncludedFolders)
                        },
                        onRestartApp = { restartApp() },
                        appLockEnabled = lockEnabled,
                        onAppLockChange = lockChange,
                        appLockTimeoutLabel = lockTimeoutLabel,
                        onAppLockTimeoutClick = lockTimeoutClick
                    )
                }
                entry<Screen.IncludedFolders> {
                    IncludedFoldersScreen(onNavigateBack = { navigateBack() })
                }
                entry<Screen.MetadataEditor> { destination ->
                    MetadataEditorScreen(destination.paths, onBack = { navigateBack() })
                }
                entry<Screen.FetchStatus> { destination ->
                    FetchStatusScreen(destination.paths, onBack = { navigateBack() })
                }
                entry<Screen.DoujinDetails> { destination ->
                    val anchor = readerReturn?.takeIf { it.galleryEntry == destination.entryId }
                    CompositionLocalProvider(LocalReaderReturnAnchor provides anchor) {
                        DoujinDetailsScreen(
                            doujinPath = destination.path,
                            onRelatedMangaClick = openDetails,
                            onTagClick = { tag ->
                                val search = Screen.SearchResults("", tag.name, false)
                                if (stack.lastOrNull() != search) stack.add(search)
                            },
                            onEditMetadata = { editMetadata(listOf(destination.path)) },
                            onFetchMetadata = { fetchMetadata(listOf(destination.path)) },
                            onNavigateBack = { navigateBack() },
                            onReadClick = { openReader(destination.path, 0, null) },
                            onImageClick = { index ->
                                readerReturn = ReaderReturnAnchor(destination.entryId, index)
                                openReader(destination.path, index, destination.entryId)
                            }
                        )
                    }
                }
                entry<Screen.DoujinReader>(
                    metadata = NavDisplay.transitionSpec {
                        val enter = slideInHorizontally(tween(220, easing = FastOutSlowInEasing)) {
                            (it * 0.06f).roundToInt()
                        } + fadeIn(tween(220, easing = FastOutSlowInEasing))
                        enter togetherWith ExitTransition.None
                    } + NavDisplay.popTransitionSpec {
                        val exit = if (readerReturn != null) {
                            fadeOut(tween(ReaderReturnDurationMillis, easing = ReaderReturnEasing))
                        } else ExitTransition.None
                        EnterTransition.None togetherWith exit
                    } + NavDisplay.predictivePopTransitionSpec {
                        val exit = if (readerReturn != null) {
                            fadeOut(tween(ReaderReturnDurationMillis, easing = ReaderReturnEasing))
                        } else ExitTransition.None
                        EnterTransition.None togetherWith exit
                    }
                ) { destination ->
                    val anchor = readerReturn?.takeIf { it.galleryEntry == destination.galleryEntry }
                    CompositionLocalProvider(LocalReaderReturnAnchor provides anchor) {
                        DoujinReaderScreen(
                            doujinPath = destination.path,
                            initialPage = destination.initialPage,
                            setReaderVolumeHandler = volumeHandler,
                            onCurrentPage = { page ->
                                destination.galleryEntry?.let { readerReturn = ReaderReturnAnchor(it, page) }
                            },
                            onRecentTabs = {
                                if (stack.lastOrNull() !is Screen.RecentTabs) {
                                    stack.add(Screen.RecentTabs(destination.path))
                                }
                            },
                            onNavigateBack = { navigateBack() }
                        )
                    }
                }
                entry<Screen.RecentTabs> { destination ->
                    RecentTabsScreen(
                        activePath = destination.activePath,
                        onNavigateBack = { navigateBack() },
                        onOpenTab = { path ->
                            if (stack.lastOrNull() == destination) {
                                stack.removeAt(stack.lastIndex)
                                if (path != destination.activePath) {
                                    if (stack.lastOrNull() is Screen.DoujinReader) stack.removeAt(stack.lastIndex)
                                    openReader(path, 0, null)
                                }
                            }
                        }
                    )
                }
                entry<Screen.CollectionDetails> { destination ->
                    CollectionDetailsScreen(
                        collectionId = destination.collectionId,
                        onNavigateBack = { navigateBack() },
                        onDoujinClick = openDetails
                    )
                }
                entry<Screen.TagDetails> { destination ->
                    TagDetailsScreen(
                        tagId = destination.tagId,
                        onDoujinClick = openDetails,
                        onNavigateBack = { navigateBack() }
                    )
                }
            }
        )
    }

    // Home is the Back destination from every other tab root. Inactive tabs retain
    // their own decorators so switching tabs keeps their ViewModels and saveable state.
    val entries = if (selectedTab == 0) tabEntries.first()
        else listOf(tabEntries.first().first()) + tabEntries[selectedTab]
    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalReaderSharedScope provides this) {
            NavDisplay(
                entries = entries,
                onBack = onBack,
                transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None }
            )
        }
    }
}
