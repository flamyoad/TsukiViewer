package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.model.CollectionWithCriterias
import com.flamyoad.tsukiviewer.repository.CollectionRepository

class CollectionViewModel(application: Application): AndroidViewModel(application) {
    private val collectionRepo: CollectionRepository

    val collectionWithCriterias: LiveData<List<CollectionWithCriterias>>

    init {
        collectionRepo = CollectionRepository(application.applicationContext)
        collectionWithCriterias = collectionRepo.getAll()
    }
}