package com.flamyoad.tsukiviewer.ui.reader.recents

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.RecentTabDao
import com.flamyoad.tsukiviewer.model.RecentTab
import kotlinx.coroutines.*

class RecentTabsViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationScope: CoroutineScope

    private val db = AppDatabase.getInstance(application)
    private val recentTabDao: RecentTabDao = db.recentTabDao()

    val toast = MutableLiveData<String>("")
    fun toast(): LiveData<String> = toast

    val tabList: LiveData<List<RecentTab>>

    var hasScrolledList: Boolean = false

    init {
        tabList = recentTabDao.getAll()
        applicationScope = (application as MyApplication).coroutineScope
    }

    fun removeRecentTab(tab: RecentTab) {
        viewModelScope.launch(Dispatchers.IO) {
            recentTabDao.delete(tab)
        }
    }

    // Clear all existing tabs except the current one in reader
    fun clearRecentTabs(exemptedTabId: Long) {
        applicationScope.launch(Dispatchers.IO) {
            val deleteCount = recentTabDao.deleteAllExcept(exemptedTabId)

            withContext(Dispatchers.Main) {
                toast.value = when (deleteCount) {
                    0 or -1 -> "No tabs cleared"
                    1 -> "1 tab cleared"
                    else -> "$deleteCount tabs cleared"
                }
                toast.value = "" // Resets the toast
            }
        }
    }
}