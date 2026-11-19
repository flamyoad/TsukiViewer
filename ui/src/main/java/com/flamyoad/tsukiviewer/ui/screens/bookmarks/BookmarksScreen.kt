package com.flamyoad.tsukiviewer.ui.screens.bookmarks

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
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
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    var showOptions by remember { mutableStateOf(false) }
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
                        expandedHeight = 56.dp,
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
                        expandedHeight = 56.dp,
                        title = { Text("Bookmarks", fontSize = 20.sp, fontWeight = FontWeight.Medium) },
                    navigationIcon = { Spacer(Modifier.width(56.dp)) },
                        actions = {
                            IconButton(onClick = { viewModel.setSearchActive(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                            Box {
                                IconButton(onClick = { showOptions = true }) {
                                    Icon(Icons.Default.MoreVert, "More options")
                                }
                                DropdownMenu(expanded = showOptions, onDismissRequest = { showOptions = false }) {
                                    DropdownMenuItem(text = { Text("Add new group") }, onClick = {
                                        showOptions = false
                                        viewModel.showCreateGroupDialog()
                                    })
                                }
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
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { scope.launch { if (uiState.items.isNotEmpty()) gridState.animateScrollToItem(0) } },
                shape = CircleShape,
                containerColor = Color(0xFFED2553),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "Scroll to top")
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
            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.fillMaxWidth().height(112.dp).background(Color(0xFF42464B))
            ) {
                item(key = "add-bookmark-group") {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(98.dp)
                            .clickable { viewModel.showCreateGroupDialog() }.padding(vertical = 12.dp)
                    ) {
                        Box(
                            Modifier.size(50.dp).background(Color(0xFFB33E5C), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) }
                        Text("Add new group", Modifier.padding(top = 16.dp), color = Color.White, fontSize = 12.sp)
                    }
                }
                items(items = uiState.groups, key = { "group-${it.name}" }) { group ->
                    BookmarkGroupChip(
                        group = group,
                        isSelected = group.name == uiState.selectedGroupName,
                        onClick = { viewModel.selectGroup(group.name) }
                    )
                }
            }
            Text(
                uiState.selectedGroupName ?: "No bookmark exists",
                Modifier.fillMaxWidth().background(Color(0xFF313335)).padding(vertical = 10.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )

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
                
                uiState.items.isEmpty() -> Unit

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = gridState,
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
                                selectionActive = uiState.isActionModeActive,
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
