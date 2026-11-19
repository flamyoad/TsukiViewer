package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.RecentTab
import com.flamyoad.tsukiviewer.core.repository.DoujinRepository
import com.flamyoad.tsukiviewer.ui.theme.ColorPrimary
import com.flamyoad.tsukiviewer.ui.theme.ColorPrimaryDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecentTabsScreen(
    activePath: String?,
    onNavigateBack: () -> Unit,
    onOpenTab: (String) -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val db = remember(application) { AppDatabase.getInstance(application) }
    val dao = db.recentTabDao()
    val flow = remember(dao) { dao.observeAll() }
    val tabs by flow.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    var pickerOpen by rememberSaveable { mutableStateOf(false) }
    val active = tabs?.firstOrNull { it.dirPath.absolutePath == activePath }

    val goBack: () -> Unit = {
        val replacement = tabs?.firstOrNull()
        if (activePath != null && active == null && replacement != null) {
            onOpenTab(replacement.dirPath.absolutePath)
        } else {
            onNavigateBack()
        }
    }
    BackHandler(onBack = goBack)

    if (pickerOpen) {
        LibraryPicker(application, db, onDismiss = { pickerOpen = false }, onOpenTab = onOpenTab)
    }

    Scaffold(
        containerColor = ColorPrimaryDark,
        topBar = {
            TopAppBar(
                title = { Text("Reader tabs") },
                navigationIcon = {
                    IconButton(onClick = goBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                actions = {
                    TextButton(onClick = { pickerOpen = true }) { Text("Open book", color = Color.White) }
                    Box {
                        IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, "More options") }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Close other tabs") },
                                enabled = active != null,
                                onClick = {
                                    menuOpen = false
                                    val keepId = active?.id ?: return@DropdownMenuItem
                                    scope.launch(Dispatchers.IO) { dao.deleteAllExcept(keepId) }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Close all tabs") },
                                onClick = {
                                    menuOpen = false
                                    scope.launch(Dispatchers.IO) { dao.deleteAllExcept(-1) }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                expandedHeight = 56.dp,
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val currentTabs = tabs
        if (currentTabs.isNullOrEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (currentTabs == null) CircularProgressIndicator(color = Color.White)
                else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No open tabs", color = Color.White)
                    Button(onClick = { pickerOpen = true }) { Text("Open book") }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(152.dp),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentTabs, key = { requireNotNull(it.id) }) { tab ->
                    Card(onClick = { onOpenTab(tab.dirPath.absolutePath) }) {
                        Box {
                            AsyncImage(
                                model = tab.thumbnail,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().aspectRatio(0.8f)
                            )
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val removed = withContext(Dispatchers.IO) { dao.delete(tab) > 0 }
                                        if (removed && snackbar.showSnackbar("Tab closed", "Undo") == SnackbarResult.ActionPerformed) {
                                            dao.restore(tab)
                                        }
                                    }
                                },
                                modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.55f))
                            ) {
                                Icon(Icons.Default.Close, "Close ${tab.title}", tint = Color.White)
                            }
                        }
                        Text(
                            text = tab.title,
                            modifier = Modifier.padding(8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryPicker(
    application: Application,
    db: AppDatabase,
    onDismiss: () -> Unit,
    onOpenTab: (String) -> Unit
) {
    var books by remember { mutableStateOf<List<RecentTab>?>(null) }
    var filter by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(db) {
        books = withContext(Dispatchers.IO) {
            DoujinRepository(application, db.doujinDetailsDao(), db.includedFolderDao())
                .localBookCovers()
                .map { (folder, cover) -> RecentTab(title = folder.name, dirPath = folder, thumbnail = cover) }
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open book") },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(value = filter, onValueChange = { filter = it }, label = { Text("Filter books") })
                }
                if (books == null) item { CircularProgressIndicator() }
                val matches = books.orEmpty().filter { it.title.contains(filter, ignoreCase = true) }
                if (books != null && matches.isEmpty()) item { Text("No books found", Modifier.padding(8.dp)) }
                items(matches, key = { it.dirPath.absolutePath }) { book ->
                    ListItem(
                        headlineContent = { Text(book.title) },
                        modifier = Modifier.clickable { onOpenTab(book.dirPath.absolutePath) }
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
