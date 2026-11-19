package com.flamyoad.tsukiviewer.ui.screens.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.Doujin
import com.flamyoad.tsukiviewer.core.model.SearchHistory
import com.flamyoad.tsukiviewer.core.repository.DoujinRepository
import com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository
import com.flamyoad.tsukiviewer.ui.navigation.Screen
import com.flamyoad.tsukiviewer.ui.screens.home.DoujinUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal data class SearchResultsState(
    val loading: Boolean = true,
    val items: List<DoujinUiModel> = emptyList(),
    val failed: Boolean = false
)

internal class SearchResultsViewModel(application: Application, query: Screen.SearchResults) : ViewModel() {
    private val mutableState = MutableStateFlow(SearchResultsState())
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(application)
                    SearchHistoryRepository(db.searchHistoryDao()).insertSearchHistory(
                        SearchHistory(title = query.title, tags = query.tags, mustIncludeAllTags = query.requireAllTags)
                    )
                    val library = DoujinRepository(application, db.doujinDetailsDao(), db.includedFolderDao())
                    if (query.tags.isBlank()) {
                        library.cachedDoujinList = library.localBookCovers().keys.mapNotNullTo(mutableListOf()) {
                            Doujin.fromFile(it, it.parentFile ?: it)
                        }
                    }
                    val matches = linkedMapOf<String, DoujinUiModel>()
                    library.scanForDoujins(query.title, query.tags, query.requireAllTags).collect { item ->
                        val path = item.path.absolutePath
                        matches.putIfAbsent(path, DoujinUiModel(
                            id = path.hashCode().toLong(),
                            coverUri = item.pic,
                            title = item.title,
                            numberOfItems = item.numberOfItems,
                            lastModified = item.lastModified,
                            path = path
                        ))
                    }
                    matches.values.sortedBy { it.title.lowercase() }
                }
                mutableState.value = SearchResultsState(loading = false, items = results)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = SearchResultsState(loading = false, failed = true)
            }
        }
    }
}
