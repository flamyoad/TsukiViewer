package com.flamyoad.tsukiviewer.ui.screens.collections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.ui.screens.collections.components.CollectionGridItem
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    onCollectionClick: (Long) -> Unit,
    onCreateCollection: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CollectionsViewModel = viewModel(
        factory = CollectionsViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show snackbar when message is available
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
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
                    placeholder = { Text("Search collections...") },
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
                    title = { Text("Collections") },
                    actions = {
                        IconButton(onClick = { viewModel.setSearchActive(true) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(
                            onClick = {
                                val newStyle = if (uiState.viewStyle == CollectionViewStyle.GRID) {
                                    CollectionViewStyle.VERTICAL_LIST
                                } else {
                                    CollectionViewStyle.GRID
                                }
                                viewModel.setViewStyle(newStyle)
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.viewStyle == CollectionViewStyle.GRID) {
                                    Icons.Default.ViewList
                                } else {
                                    Icons.Default.GridView
                                },
                                contentDescription = "Toggle View"
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateCollection
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Collection"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.collections.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) "No results found" else "No Collections",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) 
                                "Try a different search term" 
                            else 
                                "Tap + to create your first collection",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(uiState.viewStyle.getSpanCount()),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(
                        items = uiState.collections,
                        key = { it.id }
                    ) { collection ->
                        CollectionGridItem(
                            collection = collection,
                            onClick = { onCollectionClick(collection.id) },
                            onEdit = { viewModel.showEditDialog(collection.id) },
                            onDelete = { viewModel.deleteCollection(collection.id) },
                            onShowInfo = { viewModel.showInfoDialog(collection.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionsScreenPreview() {
    TsukiViewerTheme {
        CollectionsScreen(
            onCollectionClick = {}
        )
    }
}
