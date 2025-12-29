package com.flamyoad.tsukiviewer.ui.reader

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.db.dao.RecentTabDao
import com.flamyoad.tsukiviewer.model.RecentTab
import com.flamyoad.tsukiviewer.utils.FileUtils
import com.flamyoad.tsukiviewer.utils.extensions.imageExtensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class ReaderViewModel @Inject constructor(
    private val application: Application,
    private val recentTabDao: RecentTabDao
) : ViewModel() {
    private val appPreference = MyAppPreference.getInstance(application.applicationContext)

    private val directoryNoLongerExists = MutableLiveData<Boolean>(false)
    fun directoryNoLongerExists(): LiveData<Boolean> = directoryNoLongerExists

    var lastReadImagePosition: Int = 0

    private val readerMode = MutableLiveData<ReaderMode>()
    fun readerMode(): LiveData<ReaderMode> = readerMode.distinctUntilChanged()

    private val currentTab = MutableLiveData<RecentTab>()
    fun currentTab(): LiveData<RecentTab> = currentTab

    val recentTabs: LiveData<List<RecentTab>>

    init {
        recentTabs = recentTabDao.getAll()
        val defaultReaderMode = appPreference.getDefaultReaderMode()
        readerMode.value = defaultReaderMode
    }

    fun insertRecentTab(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = File(path)
            val coverImage = FileUtils.getCoverImage(dir)

            val tab = RecentTab(null, dir.name, dir, coverImage)

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

    fun setReaderMode(mode: ReaderMode) {
        readerMode.value = mode
        appPreference.setDefaultReaderMode(mode)
    }
}