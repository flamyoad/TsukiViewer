package com.flamyoad.tsukiviewer.ui.home.collections

import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.core.model.*
import com.flamyoad.tsukiviewer.core.model.Collection
import com.flamyoad.tsukiviewer.core.repository.CollectionRepository
import com.flamyoad.tsukiviewer.core.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CreateCollectionViewModel @Inject constructor(
    private val tagRepo: TagRepository,
    private val collectionRepo: CollectionRepository,
    private val includedPathDao: IncludedPathDao
) : ViewModel() {

    val includedPaths: LiveData<List<IncludedPath>> = includedPathDao.getAll()

    private val tagQuery = MutableLiveData<String>("")

    val tagList: LiveData<List<Tag>>

    private val currentCollection = MutableLiveData<Collection>()
    fun currentCollection(): LiveData<Collection> = currentCollection

    private val titles = MutableLiveData<List<String>>()
    fun titles(): LiveData<List<String>> = titles

    private val includedTags = MutableLiveData<List<Tag>>()
    fun includedTags(): LiveData<List<Tag>> = includedTags

    private val excludedTags = MutableLiveData<List<Tag>>()
    fun excludedTags(): LiveData<List<Tag>> = excludedTags

    private val dirList = MutableLiveData<List<File>>()
    fun dirList(): LiveData<List<File>> = dirList

    private val mustHaveAllIncludedTags = MutableLiveData<Boolean>()
    fun mustHaveAllIncludedTags(): LiveData<Boolean> = mustHaveAllIncludedTags

    private val mustHaveAllExcludedTags = MutableLiveData<Boolean>()
    fun mustHaveAllExcludedTags(): LiveData<Boolean> = mustHaveAllExcludedTags

    private val isInsertingData = MutableLiveData<Boolean>(false)
    fun isInsertingData(): LiveData<Boolean> = isInsertingData

    var tagPickerMode = DialogTagPicker.Mode.None

    init {
        tagList = tagQuery.switchMap {
            tagRepo.getAllWithFilter(it)
        }
    }

    fun initCollectionData(collectionId: Long) {
        if (collectionId == -1L) return

        viewModelScope.launch(Dispatchers.IO) {
            val collection = collectionRepo.get(collectionId)

            currentCollection.postValue(collection)
            titles.postValue(collectionRepo.getTitles(collectionId))
            includedTags.postValue(collectionRepo.getIncludedTags(collectionId))
            excludedTags.postValue(collectionRepo.getExcludedTags(collectionId))
            dirList.postValue(collectionRepo.getDirectories(collectionId))

            mustHaveAllIncludedTags.postValue(collection.mustHaveAllIncludedTags)
            mustHaveAllExcludedTags.postValue(collection.mustHaveAllExcludedTags)
        }
    }

    fun setQuery(query: String) {
        tagQuery.value = query
    }

    fun clearQuery() {
        tagQuery.value = ""
    }

    fun addTitle(title: String) {
        val newTitles = titles.value?.toMutableList() ?: mutableListOf()
        newTitles.add(title)

        titles.value = newTitles
    }

    fun removeTitle(title: String) {
        val newTitles = titles.value?.toMutableList() ?: mutableListOf()
        newTitles.remove(title)

        titles.value = newTitles
    }

    fun addDir(dir: File) {
        val newDirs = dirList.value?.toMutableList() ?: mutableListOf()
        if (newDirs.contains(dir)) return

        newDirs.add(dir)
        dirList.value = newDirs
    }

    fun removeDir(dir: File) {
        val newDirs = dirList.value?.toMutableList() ?: mutableListOf()
        newDirs.remove(dir)

        dirList.value = newDirs
    }

    fun addIncludedTag(tag: Tag) {
        val newTags = includedTags.value?.toMutableList() ?: mutableListOf()
        if (newTags.contains(tag)) return

        newTags.add(tag)
        includedTags.value = newTags
    }

    fun removeIncludedTag(tag: Tag) {
        val newTags = includedTags.value?.toMutableList() ?: mutableListOf()

        newTags.remove(tag)
        includedTags.value = newTags
    }

    fun addExcludedTag(tag: Tag) {
        val newTags = excludedTags.value?.toMutableList() ?: mutableListOf()
        if (newTags.contains(tag)) return

        newTags.add(tag)
        excludedTags.value = newTags
    }

    fun removeExcludedTag(tag: Tag) {
        val newTags = excludedTags.value?.toMutableList() ?: mutableListOf()

        newTags.remove(tag)
        excludedTags.value = newTags
    }

    fun setMustHaveAllIncludedTags(value: Boolean) {
        mustHaveAllIncludedTags.value = value
    }

    fun setMustHaveAllExcludedTags(value: Boolean) {
        mustHaveAllExcludedTags.value = value
    }

    fun submitCollection(collection: Collection) {
        val titleFilters = titles.value ?: emptyList()
        val includedTags = includedTags.value ?: emptyList()
        val excludedTags = excludedTags.value ?: emptyList()
        val dirFilters = dirList.value ?: emptyList()

        val criteriaList = mutableListOf<CollectionCriteria>()

        for (title in titleFilters) {
            criteriaList.add(CollectionCriteria(null, -1, CollectionCriteria.TITLE, title, title))
        }

        for (tag in includedTags) {
            criteriaList.add(CollectionCriteria(null, -1, CollectionCriteria.INCLUDED_TAGS, tag.tagId.toString(), tag.name))
        }

        for (tag in excludedTags) {
            criteriaList.add(CollectionCriteria(null, -1, CollectionCriteria.EXCLUDED_TAGS, tag.tagId.toString(), tag.name))
        }

        for (dir in dirFilters) {
            criteriaList.add(CollectionCriteria(null, -1, CollectionCriteria.DIRECTORY, dir.absolutePath, dir.name))
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                collectionRepo.insert(collection, criteriaList)
            }
        }
    }

}