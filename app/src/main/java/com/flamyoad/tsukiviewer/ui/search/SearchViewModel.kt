package com.flamyoad.tsukiviewer.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.Tag

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getInstance(application)

    private val tagDao: TagDao

    private val tagQuery = MutableLiveData<String>("")

    val tagList: LiveData<List<Tag>>

    init {
        tagDao = db.tagsDao()

        tagList = Transformations.switchMap(tagQuery) {
            return@switchMap tagDao.getAllWithFilter(it)
        }
    }

    fun setQuery(query: String) {
        tagQuery.value = query
    }

    fun clearQuery() {
        tagQuery.value = ""
    }
}