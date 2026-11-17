package com.flamyoad.tsukiviewer.ui.reader

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.core.db.dao.RecentTabDao
import com.flamyoad.tsukiviewer.core.model.RecentTab
import com.flamyoad.tsukiviewer.utils.FileUtils
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

    private val openedTab = MutableLiveData<Long?>()
    val openedTabId: LiveData<Long?> = openedTab

    val recentTabs: LiveData<List<RecentTab>>

    init {
        recentTabs = recentTabDao.getAll()
        val defaultReaderMode = appPreference.getDefaultReaderMode()
        readerMode.value = defaultReaderMode
    }

    fun insertRecentTab(path: String) {
        viewModelScope.launch {
            openedTab.value = withContext(Dispatchers.IO) {
                val dir = File(path)
                recentTabDao.open(RecentTab(null, dir.name, dir, FileUtils.getCoverImage(dir)))
            }
        }
    }

    internal fun clearOpenedTab() {
        openedTab.value = null
    }

    fun setReaderMode(mode: ReaderMode) {
        readerMode.value = mode
        appPreference.setDefaultReaderMode(mode)
    }
}