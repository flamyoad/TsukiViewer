package com.flamyoad.tsukiviewer.ui.screens.tags

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.ui.screens.tags.components.SortTagDialog
import com.flamyoad.tsukiviewer.ui.screens.tags.components.TagListItem
import com.flamyoad.tsukiviewer.ui.theme.ColorPrimaryLight
import com.flamyoad.tsukiviewer.ui.theme.SubLightTextColor
import com.flamyoad.tsukiviewer.ui.theme.TextWhite
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TagsScreen(
    onTagClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TagsViewModel = viewModel(
        factory = TagsViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var showSortDialog by remember { mutableStateOf(false) }
    
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedTabIndex,
        pageCount = { uiState.tabTypes.size }
    )
    
    // Sync tab index with pager
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setTabIndex(pagerState.currentPage)
    }
    
    // Show snackbar when message is available
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }
    
    if (showSortDialog) {
        SortTagDialog(
            currentMode = uiState.sortingMode,
            onSortModeSelected = { mode -> viewModel.setSortingMode(mode) },
            onDismiss = { showSortDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            if (uiState.isSearchActive) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = { viewModel.setSearchActive(false) },
                    active = false,
                    onActiveChange = { },
                    placeholder = { Text("Search tags...") },
                    leadingIcon = {
                        IconButton(onClick = { viewModel.setSearchActive(false) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close search"
                            )
                        }
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { }
            } else {
                TopAppBar(
                    title = { Text("Tags") },
                    actions = {
                        IconButton(onClick = { viewModel.setSearchActive(true) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = { showSortDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort"
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = ColorPrimaryLight,
                contentColor = TextWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.tabTypes.forEachIndexed { index, tagType ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { 
                            Text(
                                text = tagType.displayName,
                                color = if (pagerState.currentPage == index) TextWhite else SubLightTextColor
                            ) 
                        },
                        selectedContentColor = TextWhite,
                        unselectedContentColor = SubLightTextColor
                    )
                }
            }
            
            // Pager Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                TagsPageContent(
                    tags = uiState.filteredTags,
                    isLoading = uiState.isLoading,
                    onTagClick = onTagClick
                )
            }
        }
    }
}

@Composable
private fun TagsPageContent(
    tags: List<TagUiModel>,
    isLoading: Boolean,
    onTagClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        tags.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Tags Found",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Tags from your doujins will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.fillMaxSize()
            ) {
                items(
                    items = tags,
                    key = { it.id }
                ) { tag ->
                    TagListItem(
                        tag = tag,
                        onClick = { onTagClick(tag.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagsScreenPreview() {
    TsukiViewerTheme {
        TagsPageContent(
            tags = listOf(
                TagUiModel(1, "Artist Name", TagType.ARTISTS, 42),
                TagUiModel(2, "Character Name", TagType.CHARACTERS, 35),
                TagUiModel(3, "Parody Name", TagType.PARODIES, 28)
            ),
            isLoading = false,
            onTagClick = {}
        )
    }
}
