package com.flamyoad.tsukiviewer.ui.reader.recents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.RecentTabDao
import com.flamyoad.tsukiviewer.model.RecentTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentTabsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val recentTabDao: RecentTabDao = db.recentTabDao()

    val tabList: LiveData<List<RecentTab>>

    init {
        tabList = recentTabDao.getAll()
    }

    fun removeRecentTab(tab: RecentTab) {
        viewModelScope.launch(Dispatchers.IO) {
            recentTabDao.delete(tab)
        }
    }
}