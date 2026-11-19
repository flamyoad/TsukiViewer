package com.flamyoad.tsukiviewer.ui.screens.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import com.flamyoad.tsukiviewer.core.model.Doujin
import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.ui.screens.doujin.BookmarkGroupUiModel
import com.flamyoad.tsukiviewer.ui.screens.doujin.components.BookmarkDialog
import com.flamyoad.tsukiviewer.ui.screens.home.DoujinUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.File

@Composable
internal fun BookmarkSelectionDialog(
    items: List<DoujinUiModel>,
    onDismiss: () -> Unit,
    onSaved: (String) -> Unit
) {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getInstance(context) }
    val repository = remember(db) { BookmarkRepository(db, db.bookmarkGroupDao(), db.bookmarkItemDao()) }
    val scope = rememberCoroutineScope()
    var groups by remember { mutableStateOf(emptyList<BookmarkGroup>()) }
    var selected by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var saving by remember { mutableStateOf(false) }

    LaunchedEffect(repository) { groups = repository.getAllGroupsBlocking() }
    BookmarkDialog(
        bookmarkGroups = groups.mapIndexed { index, group ->
            BookmarkGroupUiModel(index.toLong(), group.name, isTicked = group.name in selected)
        },
        onGroupToggle = { group ->
            selected = ArrayList(if (group in selected) selected - group else selected + group)
        },
        onCreateGroup = { name ->
            val normalized = name.trim()
            if (normalized.isNotEmpty() && groups.none { it.name.equals(normalized, ignoreCase = true) }) {
                scope.launch {
                    repository.insertGroup(BookmarkGroup(normalized))
                    groups = repository.getAllGroupsBlocking()
                    selected = ArrayList(selected + normalized)
                }
            }
        },
        onDismiss = { if (!saving) onDismiss() },
        onConfirm = {
            if (selected.isNotEmpty() && !saving) {
                saving = true
                val books = items.map {
                    Doujin(it.coverUri, it.title, it.numberOfItems, it.lastModified, File(it.path))
                }
                scope.launch {
                    try {
                        onSaved(repository.insertAllItems(books, selected))
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        onSaved("Could not save bookmarks")
                    } finally {
                        saving = false
                    }
                }
            }
        }
    )
}
