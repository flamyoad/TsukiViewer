package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.model.Collection
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class DialogCollectionInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val collectionRepo = CollectionRepository(application.applicationContext)

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

    fun initCollectionInfo(collectionId: Long) {
        if (collectionId == -1L) return

        viewModelScope.launch(Dispatchers.IO) {
            currentCollection.postValue(collectionRepo.get(collectionId))
            titles.postValue(collectionRepo.getTitles(collectionId))
            includedTags.postValue(collectionRepo.getIncludedTags(collectionId))
            excludedTags.postValue(collectionRepo.getExcludedTags(collectionId))
            dirList.postValue(collectionRepo.getDirectories(collectionId))
        }
    }
}