package com.flamyoad.tsukiviewer.ui.screens.search

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.SearchHistory
import com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository
import com.flamyoad.tsukiviewer.ui.navigation.Screen
import com.flamyoad.tsukiviewer.ui.screens.home.ViewMode
import com.flamyoad.tsukiviewer.ui.screens.home.components.DoujinGridItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(
    onSearch: (SearchHistory) -> Unit
) {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getInstance(context) }
    val historyRepository = remember(db) { SearchHistoryRepository(db.searchHistoryDao()) }
    val historyFlow = remember(db) { db.searchHistoryDao().observeAll() }
    val history by historyFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var query by rememberSaveable { mutableStateOf("") }
    var tags by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var requireAll by rememberSaveable { mutableStateOf(true) }
    var initialFocusRequested by rememberSaveable { mutableStateOf(false) }
    val searchField = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if (!initialFocusRequested) {
            initialFocusRequested = true
            searchField.requestFocus()
        }
    }
    var showTagPicker by rememberSaveable { mutableStateOf(false) }

    fun submit(item: SearchHistory) {
        if (item.title.isBlank() && item.tags.isBlank()) return
        onSearch(item)
    }

    fun submitDraft() = submit(SearchHistory(
        title = query.trim(),
        tags = tags.joinToString(","),
        mustIncludeAllTags = requireAll
    ))

    if (showTagPicker) {
        SearchTagPicker(db, tags, onDismiss = { showTagPicker = false }, onSelect = {
            tags = ArrayList(it)
            showTagPicker = false
        })
    }

    Scaffold(
        topBar = {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF2B2B2B)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submitDraft() }),
                modifier = Modifier.fillMaxWidth()
                    .background(Color(0xFF313335))
                    .padding(8.dp)
                    .focusRequester(searchField)
                    .testTag("search-title"),
                decorationBox = { input ->
                    Row(
                        Modifier.fillMaxWidth().height(40.dp)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.DarkGray)
                        Box(Modifier.weight(1f)) {
                            if (query.isEmpty()) Text("Search title", fontSize = 16.sp, color = Color.DarkGray)
                            input()
                        }
                        if (query.isNotEmpty()) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear title",
                                modifier = Modifier.size(24.dp).clickable { query = "" },
                                tint = Color.DarkGray
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = ::submitDraft,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42464B)),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit-search")
            ) {
                Text("SEARCH", fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)) {
                    Text("Select tags", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    FlowRow(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp).size(32.dp)
                                .clip(CircleShape).background(Color(0xFFE0E0E0))
                                .clickable(onClickLabel = "Add tag") { showTagPicker = true },
                            contentAlignment = Alignment.Center
                        ) { Text("+", color = Color(0xFF757575)) }
                        tags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { tags = ArrayList(tags - tag) },
                                label = { Text(tag) },
                                trailingIcon = { Icon(Icons.Default.Close, "Remove $tag") }
                            )
                        }
                    }
                    if (tags.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = requireAll, onCheckedChange = { requireAll = it })
                            Text("Must include all tags", color = Color.DarkGray)
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                Text(
                    "Search history",
                    Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp),
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            items(history, key = { requireNotNull(it.id) }) { item ->
                ListItem(
                    headlineContent = { Text(item.title.ifBlank { item.tags }) },
                    supportingContent = if (item.title.isNotBlank() && item.tags.isNotBlank()) {
                        { Text(item.tags) }
                    } else null,
                    trailingContent = {
                        IconButton(onClick = { scope.launch { historyRepository.deleteSingle(item) } }) {
                            Icon(Icons.Default.Close, "Remove from history")
                        }
                    },
                    modifier = Modifier.clickable { submit(item) }
                )
            }
        }
    }
}

@Composable
private fun SearchTagPicker(
    db: AppDatabase,
    selected: List<String>,
    onDismiss: () -> Unit,
    onSelect: (List<String>) -> Unit
) {
    var filter by rememberSaveable { mutableStateOf("") }
    var selection by rememberSaveable { mutableStateOf(ArrayList(selected)) }
    var tags by remember(db) { mutableStateOf(emptyList<String>()) }
    LaunchedEffect(db) {
        tags = withContext(Dispatchers.IO) { db.tagsDao().getAllBlocking().map { it.name }.distinct() }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select tags") },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(
                        value = filter,
                        onValueChange = { filter = it },
                        label = { Text("Filter tags") },
                        singleLine = true
                    )
                }
                items(tags.filter { it.contains(filter, ignoreCase = true) }, key = { it }) { tag ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = tag in selection, onCheckedChange = { checked ->
                            selection = ArrayList(if (checked) selection + tag else selection - tag)
                        })
                        Text(tag)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSelect(selection) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
internal fun SearchResultsScreen(
    query: Screen.SearchResults,
    onDoujinClick: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val model: SearchResultsViewModel = viewModel(factory = viewModelFactory {
        initializer { SearchResultsViewModel(application, query) }
    })
    val state by model.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { SearchToolbar("Search results", onNavigateBack) }) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.items.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(if (state.failed) "Could not search the library" else "No results")
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(144.dp),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.items, key = { it.path }) { item ->
                    DoujinGridItem(item, ViewMode.SCALED, onClick = { onDoujinClick(item.path) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchToolbar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        windowInsets = WindowInsets(0, 0, 0, 0),
        expandedHeight = 56.dp
    )
}
