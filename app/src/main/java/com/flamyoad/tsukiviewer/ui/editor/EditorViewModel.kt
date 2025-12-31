package com.flamyoad.tsukiviewer.ui.editor

import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.core.db.dao.TagDao
import com.flamyoad.tsukiviewer.core.model.DoujinDetails
import com.flamyoad.tsukiviewer.core.model.EditorHistoryItem
import com.flamyoad.tsukiviewer.core.model.Mode
import com.flamyoad.tsukiviewer.core.model.Tag
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class EditorViewModel @Inject constructor(
    private val metadataRepo: MetadataRepository,
    private val doujinDetailsDao: DoujinDetailsDao,
    private val tagDao: TagDao
) : ViewModel() {

    private val undoStack = mutableListOf<EditorHistoryItem>()

    private val hasCompletedSaving = MutableLiveData<Boolean>()

    private val selectedCategory = MutableLiveData<String>("None")

    var tagsByCategory: LiveData<List<Tag>> = MutableLiveData()

    val parody = MutableLiveData<List<Tag>>()
    val character = MutableLiveData<List<Tag>>()
    val tags = MutableLiveData<List<Tag>>()
    val artist = MutableLiveData<List<Tag>>()
    val group = MutableLiveData<List<Tag>>()
    val language = MutableLiveData<List<Tag>>()
    val category = MutableLiveData<List<Tag>>()

    fun hasCompletedSaving(): LiveData<Boolean> = hasCompletedSaving

    fun selectedCategory(): LiveData<String> = selectedCategory

    private var currentPath: String = ""

    init {
        tagsByCategory = selectedCategory.switchMap { category ->
            tagDao.getByCategory(category)
        }
    }

    fun retrieveDoujinTags(dirPath: String) {
        if (currentPath == dirPath) {
            return
        } else {
            currentPath = dirPath
        }

        viewModelScope.launch(Dispatchers.IO) {
            // Dao returns null on empty result
            val details = doujinDetailsDao.getLongDetailsByPathBlocking(dirPath)

            withContext(Dispatchers.Main) {
                parody.value = details?.tags?.filter { x -> x.type == "parody" } ?: mutableListOf()

                character.value = details?.tags?.filter { x -> x.type == "character" } ?: mutableListOf()

                tags.value = details?.tags?.filter { x -> x.type == "tag" } ?: mutableListOf()

                artist.value = details?.tags?.filter { x -> x.type == "artist" } ?: mutableListOf()

                group.value = details?.tags?.filter { x -> x.type == "group" } ?: mutableListOf()

                language.value = details?.tags?.filter { x -> x.type == "language" } ?: mutableListOf()

                category.value = details?.tags?.filter { x -> x.type == "category" } ?: mutableListOf()
            }
        }
    }

    fun initEmptyTags() {
        parody.value = emptyList()
        character.value = emptyList()
        tags.value = emptyList()
        artist.value = emptyList()
        group.value = emptyList()
        language.value = emptyList()
        category.value = emptyList()
    }

    fun retrieveTagsByCategory(category: String) {
        // Triggers transformation switchMap
        selectedCategory.value = category
    }

    fun addTag(name: String, category: String) {
        val tagsLiveData = findTagListByCategory(category)
        val prevItems = tagsLiveData.value?.toMutableList() ?: mutableListOf()

        val newItem = Tag(type = category, name = name, url = "", count = 1)

        prevItems.add(newItem)

        tagsLiveData.value = prevItems

        // Push changes made to undo stack
        pushUndo(
            EditorHistoryItem(
                tag = newItem,
                index = prevItems.indexOf(newItem),
                action = Mode.ADD
            )
        )
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

        val itemToBeRemoved = prevItems[index]

        prevItems.removeAt(index)
        tagList.value = prevItems

        // Push changes made to undo stack
        pushUndo(
            EditorHistoryItem(
                tag = itemToBeRemoved,
                index = index,
                action = Mode.REMOVE
            )
        )
    }

    private fun pushUndo(item: EditorHistoryItem) {
        undoStack.add(item)
    }

    fun popUndo() {
        if (undoStack.isEmpty()) {
            return
        }

        val item = undoStack.last()
        undoStack.remove(item)
        undo(item)
    }

    private fun undo(history: EditorHistoryItem) {
        val tagsLiveData = findTagListByCategory(history.tag.type)
        val tags = tagsLiveData.value?.toMutableList() ?: mutableListOf()

        when (history.action) {
            Mode.ADD -> {
                var tagToBeRemoved: Tag? = null
                for (tag in tags) {
                    if (tag.name == history.tag.name) {
                        tagToBeRemoved = tag
                    }
                }
                tags.remove(tagToBeRemoved)
            }

            Mode.REMOVE -> {
                tags.add(history.index, history.tag)
            }
        }

        tagsLiveData.value = tags
    }

    fun saveSingleItem() {
        hasCompletedSaving.value = false

        val currentDir = File(currentPath)
        val doujinDetail = DoujinDetails(
            id = null,
            nukeCode = -1,
            shortTitleEnglish = currentDir.name,
            fullTitleEnglish = currentDir.name,
            fullTitleJapanese = "",
            absolutePath = currentDir,
            folderName = currentDir.name
        )

        val tags = listOf(
            parody.value ?: emptyList(),
            character.value ?: emptyList(),
            tags.value ?: emptyList(),
            artist.value ?: emptyList(),
            group.value ?: emptyList(),
            language.value ?: emptyList(),
            category.value ?: emptyList()
        ).flatten()

        viewModelScope.launch(Dispatchers.IO) {
            metadataRepo.saveEditedMetadata(doujinDetail, tags)

            withContext(Dispatchers.Main) {
                hasCompletedSaving.value = true
            }
        }
    }

    fun saveMultipleItems(dirPaths: List<String>) {
        hasCompletedSaving.value = false

        viewModelScope.launch(Dispatchers.IO) {
            val tags = listOf(
                parody.value ?: emptyList(),
                character.value ?: emptyList(),
                tags.value ?: emptyList(),
                artist.value ?: emptyList(),
                group.value ?: emptyList(),
                language.value ?: emptyList(),
                category.value ?: emptyList()
            ).flatten()

            for (path in dirPaths) {
                // The query uses SELECT *, so check whether list is empty to know whether item exists or not
                val previousDoujinDetails = doujinDetailsDao.findByAbsolutePath(path)

                val dir = File(path)
                val doujinDetail = when (previousDoujinDetails.isEmpty()) {
                    true -> DoujinDetails.getEmptyObject(dir)
                    false -> previousDoujinDetails.first()
                }

                metadataRepo.saveEditedMetadata(doujinDetail, tags)
            }

            withContext(Dispatchers.Main) {
                hasCompletedSaving.value = true
            }
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