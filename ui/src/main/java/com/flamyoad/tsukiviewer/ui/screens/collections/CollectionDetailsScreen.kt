package com.flamyoad.tsukiviewer.ui.screens.collections

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.Doujin
import com.flamyoad.tsukiviewer.core.repository.DoujinRepository
import com.flamyoad.tsukiviewer.ui.screens.home.DoujinUiModel
import com.flamyoad.tsukiviewer.ui.screens.home.ViewMode
import com.flamyoad.tsukiviewer.ui.screens.home.components.DoujinGridItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    collectionId: Long,
    onNavigateBack: () -> Unit,
    onDoujinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as Application
    val model: CollectionDetailsViewModel = viewModel(factory = viewModelFactory {
        initializer { CollectionDetailsViewModel(application, collectionId) }
    })
    val state by model.state.collectAsStateWithLifecycle()
    var showFilter by rememberSaveable { mutableStateOf(false) }
    var filter by rememberSaveable { mutableStateOf("") }
    val items = state.items.filter { it.title.contains(filter, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                expandedHeight = 56.dp,
                title = {
                    if (showFilter) {
                        TextField(
                            value = filter,
                            onValueChange = { filter = it },
                            placeholder = { Text("Filter collection") },
                            singleLine = true
                        )
                    } else {
                        Column {
                            Text(state.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (state.criteria.isNotEmpty()) {
                                Text(
                                    state.criteria,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showFilter) {
                            showFilter = false
                            filter = ""
                        } else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (!showFilter && state.items.isNotEmpty()) {
                        IconButton(onClick = { showFilter = true }) { Icon(Icons.Default.Search, "Filter") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        modifier = modifier
    ) { padding ->
        if (state.loading || items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (state.loading) CircularProgressIndicator()
                else Text(if (state.failed) "Could not open collection" else "No matching items")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(144.dp),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items, key = { it.path }) { item ->
                    DoujinGridItem(item, ViewMode.SCALED, onClick = { onDoujinClick(item.path) })
                }
            }
        }
    }
}

private data class CollectionDetailsState(
    val loading: Boolean = true,
    val title: String = "Collection",
    val criteria: String = "",
    val items: List<DoujinUiModel> = emptyList(),
    val failed: Boolean = false
)

private class CollectionDetailsViewModel(application: Application, collectionId: Long) : ViewModel() {
    private val mutableState = MutableStateFlow(CollectionDetailsState())
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                mutableState.value = withContext(Dispatchers.IO) {
                    loadCollection(application, collectionId)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = CollectionDetailsState(loading = false, failed = true)
            }
        }
    }

    private suspend fun loadCollection(application: Application, id: Long): CollectionDetailsState {
        val db = AppDatabase.getInstance(application)
        val info = db.collectionDao().getWithCriteria(id)
            ?: return CollectionDetailsState(loading = false, failed = true)
        val collection = info.collection
        val repository = collectionRepository(db)
        val titles = repository.getTitles(id)
        val directories = repository.getDirectories(id).map { it.canonicalPath }
        val included = repository.getIncludedTags(id)
        val excluded = repository.getExcludedTags(id)
        val library = DoujinRepository(application, db.doujinDetailsDao(), db.includedFolderDao())
        val folders = if (included.isEmpty() && excluded.isEmpty()) {
            library.localBookCovers().keys.toList()
        } else {
            val matches = when {
                collection.mustHaveAllIncludedTags && collection.mustHaveAllExcludedTags ->
                    repository.searchIncludedAndExcludedAnd(included, excluded)
                collection.mustHaveAllIncludedTags -> repository.searchIncludedAndExcludedOr(included, excluded)
                collection.mustHaveAllExcludedTags -> repository.searchIncludedOrExcludedAnd(included, excluded)
                else -> repository.searchIncludedOrExcludedOr(included, excluded)
            }
            matches.map { it.absolutePath }
        }

        val items = folders.distinct().mapNotNull { folder ->
            val titleMatch = titles.isEmpty() || if (collection.mustHaveAllTitles) {
                titles.all { folder.name.contains(it, ignoreCase = true) }
            } else {
                titles.any { folder.name.contains(it, ignoreCase = true) }
            }
            if (!titleMatch || (directories.isNotEmpty() && directories.none {
                    val path = folder.canonicalPath
                    path == it || path.startsWith(it.trimEnd(File.separatorChar) + File.separator)
                })) return@mapNotNull null
            val book = Doujin.fromFile(folder, folder.parentFile ?: folder) ?: return@mapNotNull null
            if (book.numberOfItems !in collection.minNumPages..collection.maxNumPages) return@mapNotNull null
            DoujinUiModel(
                id = folder.absolutePath.hashCode().toLong(),
                coverUri = book.pic,
                title = book.title,
                numberOfItems = book.numberOfItems,
                lastModified = book.lastModified,
                path = folder.absolutePath
            )
        }.sortedBy { it.title.lowercase() }

        return CollectionDetailsState(false, collection.name, info.getCriteriaNames(), items)
    }
}
