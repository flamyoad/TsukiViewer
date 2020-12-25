package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.TagRepository
import java.io.File

class CreateCollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getInstance(application.applicationContext)

    private val tagRepo = TagRepository(application.applicationContext)

    private val includedPathDao: IncludedPathDao

    val includedPaths: LiveData<List<IncludedPath>>

    private val tagQuery = MutableLiveData<String>("")

    val tagList: LiveData<List<Tag>>

    private val titles = MutableLiveData<List<String>>()
    fun titles(): LiveData<List<String>> = titles

    private val includedTags = MutableLiveData<List<Tag>>()
    fun includedTags(): LiveData<List<Tag>> = includedTags

    private val excludedTags = MutableLiveData<List<Tag>>()
    fun excludedTags(): LiveData<List<Tag>> = excludedTags

    private val dirList = MutableLiveData<List<File>>()
    fun dirList(): LiveData<List<File>> = dirList

    var tagPickerMode = DialogTagPicker.Mode.None

    init {
        includedPathDao = db.includedFolderDao()

        includedPaths = includedPathDao.getAll()

        tagList = Transformations.switchMap(tagQuery) {
            return@switchMap tagRepo.getAllWithFilter(it)
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

}