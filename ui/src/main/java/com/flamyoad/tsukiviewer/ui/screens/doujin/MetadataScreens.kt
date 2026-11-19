package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.DoujinDetails
import com.flamyoad.tsukiviewer.core.model.Source
import com.flamyoad.tsukiviewer.core.model.Tag
import com.flamyoad.tsukiviewer.core.model.TagType
import com.flamyoad.tsukiviewer.core.network.FetchHistory
import com.flamyoad.tsukiviewer.core.network.FetchStatus
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.EnumSet

internal fun metadataRepository(context: Context, db: AppDatabase) = MetadataRepository(
    context = context.applicationContext,
    db = db,
    pathDao = db.includedFolderDao(),
    doujinDetailsDao = db.doujinDetailsDao(),
    tagDao = db.tagsDao(),
    doujinTagDao = db.doujinTagDao(),
    folderDao = db.folderDao()
)

private val metadataCategories = TagType.entries.filter { it != TagType.All }

@Composable
internal fun MetadataEditorScreen(paths: List<String>, onBack: () -> Unit) {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getInstance(context) }
    val repository = remember(db) { metadataRepository(context, db) }
    val scope = rememberCoroutineScope()
    var fields by rememberSaveable { mutableStateOf<ArrayList<String>?>(null) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(paths) {
        if (fields != null) return@LaunchedEffect
        try {
            val saved = withContext(Dispatchers.IO) {
                paths.singleOrNull()?.let { db.doujinDetailsDao().getLongDetailsByPathBlocking(it) }
            }
            fields = ArrayList(metadataCategories.map { category ->
                saved?.tags.orEmpty().filter { it.type == category.getLowerCaseName() }
                    .joinToString("\n") { it.name }
            })
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            error = "Could not load tags"
        }
    }

    fun save() {
        val draft = fields ?: return
        val tags = metadataCategories.zip(draft).flatMap { (category, lines) ->
            lines.lineSequence().map(String::trim).filter(String::isNotEmpty).distinct().map { name ->
                Tag(type = category.getLowerCaseName(), name = name, url = "", count = 1)
            }.toList()
        }
        saving = true
        scope.launch {
            try {
                for (path in paths) {
                    val previous = withContext(Dispatchers.IO) {
                        db.doujinDetailsDao().getLongDetailsByPathBlocking(path)?.doujinDetails
                    }
                    repository.saveEditedMetadata(previous ?: DoujinDetails.getEmptyObject(File(path)), tags)
                }
                onBack()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                error = "Could not save tags"
            } finally {
                saving = false
            }
        }
    }

    Scaffold(topBar = {
        MetadataToolbar(
            title = "Tag Editor",
            subtitle = paths.singleOrNull()?.let { File(it).name } ?: "${paths.size} selected",
            onBack = { if (!saving) onBack() },
            action = { TextButton(onClick = ::save, enabled = fields != null && !saving) {
                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
            } }
        )
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            val draft = fields
            if (draft == null) {
                if (error == null) CircularProgressIndicator()
            } else {
                metadataCategories.forEachIndexed { index, category ->
                    OutlinedTextField(
                        value = draft[index],
                        onValueChange = { text ->
                            fields = ArrayList(draft).apply { this[index] = text }
                        },
                        label = { Text(category.name) },
                        placeholder = { Text("One tag per line") },
                        enabled = !saving,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private data class FetchUiState(
    val current: String? = null,
    val history: List<FetchHistory> = emptyList()
)

private class FetchViewModel(application: Application, paths: List<String>) : ViewModel() {
    private val mutableState = MutableStateFlow(FetchUiState())
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val repository = metadataRepository(application, AppDatabase.getInstance(application))
            for (path in paths) {
                val dir = File(path)
                mutableState.update { it.copy(current = path) }
                val item = try {
                    repository.fetchMetadata(dir, EnumSet.of(Source.NHentai))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    FetchHistory(dir, dir.name, FetchStatus.NETWORK_ERROR)
                }
                mutableState.update { it.copy(history = it.history + item) }
            }
            mutableState.update { it.copy(current = null) }
        }
    }
}

@Composable
internal fun FetchStatusScreen(paths: List<String>, onBack: () -> Unit) {
    val application = LocalContext.current.applicationContext as Application
    val model: FetchViewModel = viewModel(factory = viewModelFactory {
        initializer { FetchViewModel(application, paths) }
    })
    val state by model.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { MetadataToolbar("Fetching metadata", onBack = onBack) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${state.history.size} / ${paths.size} processed")
                    LinearProgressIndicator(
                        progress = { state.history.size.toFloat() / paths.size.coerceAtLeast(1) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.current?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
            items(state.history, key = { it.dir.absolutePath }) { item ->
                ListItem(
                    headlineContent = { Text(item.doujinName) },
                    supportingContent = { Text(when (item.status) {
                        FetchStatus.SUCCESS -> "Success"
                        FetchStatus.NO_MATCH -> "No match"
                        FetchStatus.ALREADY_EXISTS -> "Already exists"
                        FetchStatus.NETWORK_ERROR -> "Network error"
                        FetchStatus.NONE -> "Skipped"
                    }) }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetadataToolbar(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    action: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(title)
                subtitle?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
        actions = { action() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        expandedHeight = 56.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}
