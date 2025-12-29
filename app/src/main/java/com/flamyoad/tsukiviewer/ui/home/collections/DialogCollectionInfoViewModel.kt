package com.flamyoad.tsukiviewer.ui.home.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.model.Collection
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class DialogCollectionInfoViewModel @Inject constructor(
    private val collectionRepo: CollectionRepository
) : ViewModel() {

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