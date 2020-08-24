package com.flamyoad.tsukiviewer.ui.editor

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.EditorHistoryItem
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase

    private val doujinDetailsDao: DoujinDetailsDao

    private val tagDao: TagDao

    private val metadataRepo = MetadataRepository(application)

    private val undoStack = mutableListOf<EditorHistoryItem>()

    private val _selectedCategory = MutableLiveData<String>("None")
    val selectedCategory: LiveData<String> = _selectedCategory

    var tagsByCategory: LiveData<List<Tag>> =  MutableLiveData()

    val parody = MutableLiveData<List<Tag>>()
    val character = MutableLiveData<List<Tag>>()
    val tags = MutableLiveData<List<Tag>>()
    val artist = MutableLiveData<List<Tag>>()
    val group = MutableLiveData<List<Tag>>()
    val language = MutableLiveData<List<Tag>>()
    val category = MutableLiveData<List<Tag>>()

    private var currentPath: String = ""

    init {
        db = AppDatabase.getInstance(application)
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()

        tagsByCategory = Transformations.switchMap(_selectedCategory) { category ->
            return@switchMap tagDao.getByCategory(category)
        }
    }

    fun retrieveDoujinTags(dirPath: String) {
        if (currentPath == dirPath) {
            return
        } else {
            currentPath = dirPath
        }

        viewModelScope.launch(Dispatchers.IO) {
            val details = doujinDetailsDao.getLongDetailsByPathBlocking(dirPath)

            withContext(Dispatchers.Main) {
                parody.value = details.tags.filter { x -> x.type == "parody" } ?: mutableListOf()

                character.value = details.tags.filter { x -> x.type == "character" } ?: mutableListOf()

                tags.value = details.tags.filter { x -> x.type == "tag" } ?: mutableListOf()

                artist.value = details.tags.filter { x -> x.type == "artist" } ?: mutableListOf()

                group.value = details.tags.filter { x -> x.type == "group" } ?: mutableListOf()

                language.value = details.tags.filter { x -> x.type == "language" } ?: mutableListOf()

                category.value = details.tags.filter { x -> x.type == "category" } ?: mutableListOf()
            }
        }
    }

    fun retrieveTagsByCategory(category: String) {
        // Triggers transformation switchMap
        _selectedCategory.value = category
    }

    fun addTag(name: String, category: String) {
        val tagList = findTagListByCategory(category)
        val prevItems = tagList.value?.toMutableList() ?: mutableListOf()

        val newItem = Tag(type = category, name = name, url = "",  count = 1)

        prevItems.add(newItem)

        tagList.value = prevItems
    }

    fun removeTag(name: String, category: String) {
        val tagList = findTagListByCategory(category)
        val prevItems = tagList.value?.toMutableList() ?: mutableListOf()

        var index = -1
        for (i in prevItems.indices) {
            if (prevItems[i].name == name) {
                index = i
            }
        }

        prevItems.removeAt(index)
        tagList.value = prevItems
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

    private fun findTagListByCategory(categoryName: String): MutableLiveData<List<Tag>> {
        return when (categoryName) {
            "parody" -> parody
            "character" -> character
            "tag" -> tags
            "artist" -> artist
            "group" -> group
            "language" -> language
            "category" -> category
            else -> throw IllegalArgumentException("Category does not exist")
        }
    }

}