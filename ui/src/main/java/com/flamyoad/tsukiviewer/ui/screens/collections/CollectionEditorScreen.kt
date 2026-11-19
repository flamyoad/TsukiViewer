package com.flamyoad.tsukiviewer.ui.screens.collections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.Collection
import com.flamyoad.tsukiviewer.core.model.CollectionCriteria
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CollectionEditorScreen(collectionId: Long?, onBack: () -> Unit) {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getInstance(context) }
    val repository = remember(db) { collectionRepository(db) }
    val scope = rememberCoroutineScope()
    var initialized by rememberSaveable { mutableStateOf(collectionId == null) }
    var name by rememberSaveable { mutableStateOf("") }
    var titles by rememberSaveable { mutableStateOf("") }
    var minPages by rememberSaveable { mutableStateOf("") }
    var maxPages by rememberSaveable { mutableStateOf("") }
    var allTitles by rememberSaveable { mutableStateOf(true) }
    var allIncluded by rememberSaveable { mutableStateOf(false) }
    var allExcluded by rememberSaveable { mutableStateOf(false) }
    var included by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var excluded by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var directories by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var picker by rememberSaveable { mutableStateOf<String?>(null) }
    var savedLabels by remember { mutableStateOf(emptyMap<String, String>()) }
    var availableTags by remember { mutableStateOf(emptyMap<String, String>()) }
    var availableDirectories by remember { mutableStateOf(emptyMap<String, String>()) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(db, collectionId) {
        try {
            val record = withContext(Dispatchers.IO) {
                availableDirectories = db.includedFolderDao().getAllBlocking()
                    .associate { it.absolutePath to it.absolutePath }
                availableTags = db.tagsDao().getAllBlocking().associate { it.tagId.toString() to it.name }
                collectionId?.let { db.collectionDao().getWithCriteria(it) }
            }
            if (collectionId != null && record == null) {
                error = "Collection no longer exists"
                return@LaunchedEffect
            }
            savedLabels = record?.criteriaList.orEmpty().associate { it.value to it.valueName }
            if (record != null && !initialized) {
                val collection = record.collection
                val criteria = record.criteriaList.groupBy { it.type }
                name = collection.name
                minPages = collection.minNumPages.takeUnless { it == Int.MIN_VALUE }?.toString().orEmpty()
                maxPages = collection.maxNumPages.takeUnless { it == Int.MAX_VALUE }?.toString().orEmpty()
                allTitles = collection.mustHaveAllTitles
                allIncluded = collection.mustHaveAllIncludedTags
                allExcluded = collection.mustHaveAllExcludedTags
                titles = criteria[CollectionCriteria.TITLE].orEmpty().joinToString("\n") { it.value }
                included = ArrayList(criteria[CollectionCriteria.INCLUDED_TAGS].orEmpty().map { it.value })
                excluded = ArrayList(criteria[CollectionCriteria.EXCLUDED_TAGS].orEmpty().map { it.value })
                directories = ArrayList(criteria[CollectionCriteria.DIRECTORY].orEmpty().map { it.value })
                initialized = true
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            error = "Could not load collection settings"
        }
    }

    val minimum = if (minPages.isBlank()) Int.MIN_VALUE else minPages.toIntOrNull()
    val maximum = if (maxPages.isBlank()) Int.MAX_VALUE else maxPages.toIntOrNull()
    val validPages = minimum != null && maximum != null && minimum <= maximum
    val titleKeywords = titles.lineSequence().map(String::trim).filter(String::isNotEmpty).distinct().toList()

    fun save() {
        if (name.isBlank() || !validPages || saving) return
        val collection = Collection(
            id = collectionId,
            name = name.trim(),
            coverPhoto = File(""),
            mustHaveAllTitles = allTitles,
            mustHaveAllIncludedTags = allIncluded,
            mustHaveAllExcludedTags = allExcluded,
            minNumPages = requireNotNull(minimum),
            maxNumPages = requireNotNull(maximum)
        )
        val criteria = buildList {
            titleKeywords.forEach { add(CollectionCriteria(null, -1, CollectionCriteria.TITLE, it, it)) }
            included.forEach {
                add(CollectionCriteria(null, -1, CollectionCriteria.INCLUDED_TAGS, it, availableTags[it] ?: savedLabels[it] ?: it))
            }
            excluded.forEach {
                add(CollectionCriteria(null, -1, CollectionCriteria.EXCLUDED_TAGS, it, availableTags[it] ?: savedLabels[it] ?: it))
            }
            directories.forEach { add(CollectionCriteria(null, -1, CollectionCriteria.DIRECTORY, it, File(it).name)) }
        }
        saving = true
        scope.launch {
            try {
                repository.insert(collection, criteria)
                onBack()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                error = "Could not save the collection"
            } finally {
                saving = false
            }
        }
    }

    val activePicker = picker
    if (activePicker != null) {
        val selected = when (activePicker) {
            CollectionCriteria.INCLUDED_TAGS -> included
            CollectionCriteria.EXCLUDED_TAGS -> excluded
            else -> directories
        }
        val options = if (activePicker == CollectionCriteria.DIRECTORY) availableDirectories else availableTags
        CriteriaPicker(
            title = when (activePicker) {
                CollectionCriteria.INCLUDED_TAGS -> "Included tags"
                CollectionCriteria.EXCLUDED_TAGS -> "Excluded tags"
                else -> "Directories"
            },
            options = selected.associateWith { savedLabels[it] ?: it } + options,
            selected = selected,
            onDismiss = { picker = null },
            onSelect = {
                when (activePicker) {
                    CollectionCriteria.INCLUDED_TAGS -> included = it
                    CollectionCriteria.EXCLUDED_TAGS -> excluded = it
                    else -> directories = it
                }
                picker = null
            }
        )
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (collectionId == null) "Create collection" else "Edit collection") },
            navigationIcon = { IconButton(onClick = onBack, enabled = !saving) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            } },
            actions = {
                TextButton(onClick = ::save, enabled = initialized && name.isNotBlank() && validPages && !saving) {
                    Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            expandedHeight = 56.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        )
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (!initialized) {
                if (error == null) CircularProgressIndicator()
                return@Column
            }
            OutlinedTextField(name, { name = it }, label = { Text("Collection name") },
                singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(titles, { titles = it }, label = { Text("Title keywords (one per line)") },
                modifier = Modifier.fillMaxWidth())
            CriteriaCheckbox("Must match all titles", allTitles) { allTitles = it }
            TextButton(onClick = { picker = CollectionCriteria.INCLUDED_TAGS }) {
                Text("Included tags (${included.size})")
            }
            CriteriaCheckbox("Must include all selected tags", allIncluded) { allIncluded = it }
            TextButton(onClick = { picker = CollectionCriteria.EXCLUDED_TAGS }) {
                Text("Excluded tags (${excluded.size})")
            }
            CriteriaCheckbox("Must exclude all selected tags", allExcluded) { allExcluded = it }
            TextButton(onClick = { picker = CollectionCriteria.DIRECTORY }) {
                Text("Directories (${directories.size})")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(minPages, { minPages = it }, label = { Text("Minimum pages") },
                    singleLine = true, modifier = Modifier.weight(1f), isError = !validPages,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(maxPages, { maxPages = it }, label = { Text("Maximum pages") },
                    singleLine = true, modifier = Modifier.weight(1f), isError = !validPages,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }
}

@Composable
private fun CriteriaPicker(
    title: String,
    options: Map<String, String>,
    selected: List<String>,
    onDismiss: () -> Unit,
    onSelect: (ArrayList<String>) -> Unit
) {
    var selection by rememberSaveable { mutableStateOf(ArrayList(selected)) }
    var query by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(query, { query = it }, label = { Text("Filter") }, singleLine = true)
                }
                items(options.entries.filter { it.value.contains(query, ignoreCase = true) }, key = { it.key }) { (id, label) ->
                    CriteriaCheckbox(label, id in selection) { checked ->
                        selection = ArrayList(if (checked) selection + id else selection - id)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSelect(selection) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun CriteriaCheckbox(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, onChange)
        Text(label)
    }
}
