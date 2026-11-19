package com.flamyoad.tsukiviewer.ui.screens.tags

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.saveable.rememberSaveable
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
    
    var showSortDialog by rememberSaveable { mutableStateOf(false) }
    var tagToDelete by rememberSaveable { mutableStateOf<Long?>(null) }
    uiState.allTags.firstOrNull { it.id == tagToDelete }?.let { tag ->
        AlertDialog(
            onDismissRequest = { tagToDelete = null },
            title = { Text("Delete tag?") },
            text = { Text(tag.name) },
            confirmButton = { TextButton(onClick = {
                tagToDelete = null
                viewModel.deleteTag(tag.id)
            }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { tagToDelete = null }) { Text("Cancel") } }
        )
    }
    
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
                    expandedHeight = 56.dp,
                    title = { Text("Tags", fontSize = 20.sp, fontWeight = FontWeight.Medium) },
                    navigationIcon = { Spacer(Modifier.width(56.dp)) },
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
                        containerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    windowInsets = WindowInsets(
                        top = 0.dp,
                        bottom = 0.dp,
                    )
                )
            }
        },
        containerColor = Color(0xFFECECEC),
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = TextWhite,
                edgePadding = 0.dp,
                divider = {},
                indicator = { positions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(positions[pagerState.currentPage]),
                        height = 2.dp,
                        color = Color.White
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.tabTypes.forEachIndexed { index, tagType ->
                    Tab(
                        modifier = Modifier.height(56.dp),
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
                    onTagClick = onTagClick,
                    onDeleteClick = { tagToDelete = it }
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
    modifier: Modifier = Modifier,
    onDeleteClick: (Long) -> Unit = {}
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
        
        tags.isEmpty() -> Unit

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
                        onClick = { onTagClick(tag.id) },
                        onLongClick = { onDeleteClick(tag.id) }
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
