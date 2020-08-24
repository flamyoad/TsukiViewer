package com.flamyoad.tsukiviewer.ui.editor

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.model.EditorHistoryItem
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase

    private val doujinDetailsDao: DoujinDetailsDao

    private val tagDao: TagDao

    private val metadataRepo = MetadataRepository(application)

    private val undoStack = mutableListOf<EditorHistoryItem>()

    lateinit var detailWithTags: LiveData<DoujinDetailsWithTags>

    private val selectedCategory = MutableLiveData<String>("None")

    var tagsByCategory: LiveData<List<Tag>> =  MutableLiveData()

    init {
        db = AppDatabase.getInstance(application)
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()

        tagsByCategory = Transformations.switchMap(selectedCategory) { category ->
            return@switchMap tagDao.getByCategory(category)
        }
    }

    fun retrieveDoujinTags(dirPath: String) {
        detailWithTags = doujinDetailsDao.getLongDetailsByPath(dirPath)
    }

    fun retrieveTagsByCategory(category: String) {
        selectedCategory.value = category
    }

    suspend fun findTagByName(type: String, name: String): Tag? {
        return tagDao.get(type, name)
    }

    fun pushUndo(item: EditorHistoryItem) {
        undoStack.add(item)
    }

    fun popUndo(): EditorHistoryItem? {
        if (undoStack.isEmpty()) {
            return null
        }

        val item = undoStack.last()
        undoStack.remove(item)

        return item
    }

    fun save(doujinDetails: DoujinDetails, tags: List<Tag>) {
        viewModelScope.launch(Dispatchers.IO) {
            metadataRepo.storeMetadata(doujinDetails, tags)
        }
    }

}