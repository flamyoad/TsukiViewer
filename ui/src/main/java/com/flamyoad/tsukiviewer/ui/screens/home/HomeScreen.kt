package com.flamyoad.tsukiviewer.ui.screens.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.ui.screens.home.components.DoujinGridItem
import com.flamyoad.tsukiviewer.ui.screens.home.components.SortDoujinDialog
import com.flamyoad.tsukiviewer.ui.screens.home.components.ViewModeMenu
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDoujinClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showSortDialog by remember { mutableStateOf(false) }
    var showViewModeMenu by remember { mutableStateOf(false) }
    
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val spanCount = uiState.viewMode.getSpanCount(isPortrait)
    
    val gridState = rememberLazyGridState()
    
    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToDoujinDetails -> {
                    onDoujinClick(event.doujinPath)
                }
                is HomeEvent.NavigateToSearch -> {
                    onSearchClick()
                }
                is HomeEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    
    // Handle snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }
    
    // Sort dialog
    if (showSortDialog) {
        SortDoujinDialog(
            currentSortMode = uiState.sortMode,
            onSortModeSelected = { mode ->
                viewModel.setSortMode(mode)
            },
            onDismiss = { showSortDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            if (uiState.isActionModeActive) {
                // Action mode top bar
                ActionModeTopBar(
                    selectedCount = uiState.selectedCount,
                    onClose = { viewModel.cancelActionMode() },
                    onBookmark = { viewModel.bookmarkSelectedDoujins() },
                    onEdit = { viewModel.editSelectedDoujins() }
                )
            } else {
                // Normal top bar
                HomeTopBar(
                    isLoading = uiState.isLoading,
                    onSearchClick = onSearchClick,
                    onSyncClick = { viewModel.reloadDoujins() },
                    onSortClick = { showSortDialog = true },
                    onViewModeClick = { showViewModeMenu = true },
                    showViewModeMenu = showViewModeMenu,
                    currentViewMode = uiState.viewMode,
                    onViewModeSelected = { mode ->
                        viewModel.setViewMode(mode)
                        showViewModeMenu = false
                    },
                    onDismissViewModeMenu = { showViewModeMenu = false }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.doujinList.isEmpty() -> {
                    // Initial loading state
                    LoadingContent()
                }
                
                uiState.doujinList.isEmpty() -> {
                    // Empty state
                    EmptyContent(
                        onRefresh = { viewModel.reloadDoujins() }
                    )
                }
                
                else -> {
                    // Grid content
                    DoujinGrid(
                        doujinList = uiState.doujinList,
                        viewMode = uiState.viewMode,
                        spanCount = spanCount,
                        isSorting = uiState.isSorting,
                        gridState = gridState,
                        onDoujinClick = { doujin -> viewModel.onDoujinClick(doujin) },
                        onDoujinLongClick = { doujin -> viewModel.onDoujinLongClick(doujin) }
                    )
                }
            }
            
            // Sorting indicator overlay
            AnimatedVisibility(
                visible = uiState.isSorting,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                SortingIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    isLoading: Boolean,
    onSearchClick: () -> Unit,
    onSyncClick: () -> Unit,
    onSortClick: () -> Unit,
    onViewModeClick: () -> Unit,
    showViewModeMenu: Boolean,
    currentViewMode: ViewMode,
    onViewModeSelected: (ViewMode) -> Unit,
    onDismissViewModeMenu: () -> Unit
) {
    TopAppBar(
        title = { Text("Local Storage") },
        windowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp,
        ), actions = {
            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp),
                    strokeWidth = 2.dp
                )
            }
            
            // Search
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            
            // Sync/Refresh
            IconButton(onClick = onSyncClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync"
                )
            }
            
            // Sort
            IconButton(onClick = onSortClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort"
                )
            }
            
            // View mode
            Box {
                IconButton(onClick = onViewModeClick) {
                    Icon(
                        imageVector = Icons.Default.ViewModule,
                        contentDescription = "View Mode"
                    )
                }
                
                ViewModeMenu(
                    expanded = showViewModeMenu,
                    currentViewMode = currentViewMode,
                    onViewModeSelected = onViewModeSelected,
                    onDismiss = onDismissViewModeMenu
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionModeTopBar(
    selectedCount: Int,
    onClose: () -> Unit,
    onBookmark: () -> Unit,
    onEdit: () -> Unit
) {
    TopAppBar(
        title = { Text("$selectedCount selected") },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        },
        actions = {
            IconButton(onClick = onBookmark) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        windowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp,
        )
    )
}

@Composable
private fun DoujinGrid(
    doujinList: List<DoujinUiModel>,
    viewMode: ViewMode,
    spanCount: Int,
    isSorting: Boolean,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
    onDoujinClick: (DoujinUiModel) -> Unit,
    onDoujinLongClick: (DoujinUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(spanCount),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .alpha(if (isSorting) 0.6f else 1f)
    ) {
        items(
            items = doujinList,
            key = { it.id }
        ) { doujin ->
            DoujinGridItem(
                doujin = doujin,
                viewMode = viewMode,
                onClick = { onDoujinClick(doujin) },
                onLongClick = { onDoujinLongClick(doujin) }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading doujins...",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun EmptyContent(
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No doujins found",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Add directories in Settings to scan for doujins",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    }
}

@Composable
private fun SortingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = "Sorting...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TsukiViewerTheme {
        // Preview requires ViewModel injection
    }
}
