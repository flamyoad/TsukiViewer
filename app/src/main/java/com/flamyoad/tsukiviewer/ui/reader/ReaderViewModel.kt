package com.flamyoad.tsukiviewer.ui.reader

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.model.RecentTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val directoryNoLongerExists = MutableLiveData<Boolean>(false)
    fun directoryNoLongerExists(): LiveData<Boolean> = directoryNoLongerExists

    private val db = AppDatabase.getInstance(application)

    val recentTabDao = db.recentTabDao()

    var currentImagePosition: Int = 0

    private val currentTab = MutableLiveData<RecentTab>()
    fun currentTab(): LiveData<RecentTab> = currentTab

    val recentTabs: LiveData<List<RecentTab>>

    init {
        recentTabs = db.recentTabDao().getAll()
    }

    fun insertRecentTab(path: String) {
        val dir = File(path)
        val tab = RecentTab(null, dir.name, File(path), File(path))

        viewModelScope.launch(Dispatchers.IO) {
            val lastExistingTab = recentTabDao.getByPath(path)
            if (lastExistingTab != null) {
                withContext(Dispatchers.Main) {
                    currentTab.value = lastExistingTab
                }
                return@launch
            }

            val insertId = recentTabDao.insert(tab)
            val newlyInsertedTab = recentTabDao.get(insertId)
            withContext(Dispatchers.Main) {
                currentTab.value = newlyInsertedTab
            }
        }
    }

    fun setCurrentTab(tabId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val tab = recentTabDao.get(tabId)

            withContext(Dispatchers.Main) {
                currentTab.value = tab
            }
        }
    }
}