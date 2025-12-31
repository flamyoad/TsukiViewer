package com.flamyoad.tsukiviewer.ui.screens.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.ui.screens.bookmarks.components.BookmarkGroupChip
import com.flamyoad.tsukiviewer.ui.screens.bookmarks.components.BookmarkItemCard
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onDoujinClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = viewModel(
        factory = BookmarksViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
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
    
    // Create group dialog
    if (uiState.showCreateGroupDialog) {
        CreateGroupDialog(
            onDismiss = { viewModel.hideCreateGroupDialog() },
            onCreate = { name -> viewModel.createNewGroup(name) }
        )
    }
    
    Scaffold(
        topBar = {
            when {
                uiState.isActionModeActive -> {
                    // Action mode top bar
                    TopAppBar(
                        title = { Text("${uiState.selectedCount} selected") },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.cancelActionMode() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.deleteSelectedItems() }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
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
                uiState.isSearchActive -> {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onSearch = { viewModel.setSearchActive(false) },
                        active = false,
                        onActiveChange = { },
                        placeholder = { Text("Search bookmarks...") },
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
                }
                else -> {
                    // Normal top bar
                    TopAppBar(
                        title = { Text("Bookmarks") },
                        actions = {
                            IconButton(onClick = { viewModel.setSearchActive(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                            IconButton(onClick = { viewModel.showCreateGroupDialog() }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Group"
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
            // Groups horizontal list
            if (uiState.groups.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = uiState.groups,
                        key = { it.name }
                    ) { group ->
                        BookmarkGroupChip(
                            group = group,
                            isSelected = group.name == uiState.selectedGroupName,
                            onClick = { viewModel.selectGroup(group.name) }
                        )
                    }
                }
            }
            
            // Content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.groups.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Bookmarks",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Bookmark your favorite doujins to see them here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                uiState.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) "No results" else "Empty Group",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "This group has no bookmarked items",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.items,
                            key = { it.id }
                        ) { item ->
                            BookmarkItemCard(
                                item = item,
                                onClick = {
                                    if (uiState.isActionModeActive) {
                                        viewModel.onItemClick(item)
                                    } else {
                                        onDoujinClick(item.absolutePath)
                                    }
                                },
                                onLongClick = { viewModel.onItemLongClick(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarksScreenPreview() {
    TsukiViewerTheme {
        // Preview content would go here
    }
}

@Composable
private fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Group") },
        text = {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (groupName.isNotBlank()) {
                        onCreate(groupName.trim())
                    }
                },
                enabled = groupName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
