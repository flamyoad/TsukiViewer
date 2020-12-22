package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flamyoad.tsukiviewer.model.Tag

class CreateCollectionViewModel(application: Application) : AndroidViewModel(application) {

    private val titles = MutableLiveData<List<String>>()
    fun titles(): LiveData<List<String>> = titles

    private val includedTags = MutableLiveData<List<Tag>>()
    fun includedTags(): LiveData<List<Tag>> = includedTags

    private val excludedTags = MutableLiveData<List<Tag>>()
    fun excludedTags(): LiveData<List<Tag>> = excludedTags

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
}