package com.flamyoad.tsukiviewer.ui.screens.collections

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.CollectionCriteria
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference
import com.flamyoad.tsukiviewer.core.repository.CollectionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal fun collectionRepository(db: AppDatabase) = CollectionRepository(
    db = db,
    collectionDao = db.collectionDao(),
    criteriaDao = db.collectionCriteriaDao(),
    collectionDoujinDao = db.collectionDoujinDao()
)

class CollectionsViewModel(application: Application) : ViewModel() {
    private val db = AppDatabase.getInstance(application)
    private val repository = collectionRepository(db)
    private val preferences = MyAppPreference.getInstance(application)
    private val mutableState = MutableStateFlow(CollectionsUiState(
        isLoading = true,
        viewStyle = if (preferences.getCollectionViewStyle() == 0) CollectionViewStyle.VERTICAL_LIST
            else CollectionViewStyle.GRID
    ))
    val uiState = mutableState.asStateFlow()
    private var allCollections = emptyList<CollectionUiModel>()

    init {
        viewModelScope.launch {
            db.collectionDao().observeAllWithCriteria()
                .catch { mutableState.update { it.copy(isLoading = false, snackbarMessage = "Failed to load collections") } }
                .collect { records ->
                    allCollections = records.map { record ->
                        val collection = record.collection
                        CollectionUiModel(
                            id = requireNotNull(collection.id),
                            title = collection.name,
                            coverImageUri = collection.coverPhoto.takeIf { it.isFile }?.toUri(),
                            tags = record.criteriaList.filter {
                                it.type == CollectionCriteria.INCLUDED_TAGS || it.type == CollectionCriteria.EXCLUDED_TAGS
                            }.map { it.valueName },
                            criteriaSummary = record.getCriteriaNames()
                        )
                    }
                    mutableState.update { it.copy(collections = matches(it.searchQuery), isLoading = false) }
                }
        }
    }

    fun setSearchQuery(query: String) {
        mutableState.update { it.copy(searchQuery = query, collections = matches(query)) }
    }

    private fun matches(query: String) = allCollections.filter {
        it.title.contains(query, ignoreCase = true)
    }

    fun setViewStyle(style: CollectionViewStyle) {
        preferences.setCollectionViewStyle(if (style == CollectionViewStyle.VERTICAL_LIST) 0 else 1)
        mutableState.update { it.copy(viewStyle = style) }
    }

    fun setSearchActive(active: Boolean) {
        mutableState.update { it.copy(isSearchActive = active) }
        if (!active) setSearchQuery("")
    }

    fun deleteCollection(id: Long) {
        viewModelScope.launch {
            try {
                repository.delete(id)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(snackbarMessage = "Failed to delete collection") }
            }
        }
    }

    fun clearSnackbar() {
        mutableState.update { it.copy(snackbarMessage = null) }
    }
}

class CollectionsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == CollectionsViewModel::class.java)
        return CollectionsViewModel(application) as T
    }
}
