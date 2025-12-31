package com.flamyoad.tsukiviewer.ui.settings

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao
import com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao
import com.flamyoad.tsukiviewer.core.db.dao.TagDao
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val application: Application,
    private val db: AppDatabase,
    private val doujinDetailsDao: DoujinDetailsDao,
    private val doujinTagsDao: DoujinTagsDao,
    private val tagDao: TagDao,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {
    
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    private val isRemovingItems = MutableLiveData<Boolean>()
    fun isRemovingItems(): LiveData<Boolean> = isRemovingItems

    private val packageAppList = MutableLiveData<List<ResolveInfo>>()
    fun packageAppList(): LiveData<List<ResolveInfo>> = packageAppList

    init {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
        }
        val pkgAppList: List<ResolveInfo> =
            application.packageManager.queryIntentActivities(intent, 0)
        packageAppList.value = pkgAppList
    }

    fun setThirdPartyGallery(packageName: String) {
        prefs.edit()
            .putString(MainPreferences.EXTERNAL_GALLERY_PKG_NAME, packageName)
            .apply()
    }

    fun clearMetadata() {
        isRemovingItems.value = true

        viewModelScope.launch(Dispatchers.IO) {
            doujinDetailsDao.deleteAll()

            withContext(Dispatchers.Main) {
                isRemovingItems.value = false
            }
        }
    }

    fun clearTags() {
        isRemovingItems.value = true

        viewModelScope.launch(Dispatchers.IO) {
            db.withTransaction {
                doujinTagsDao.deleteAll()
                tagDao.deleteAll()

                withContext(Dispatchers.Main) {
                    isRemovingItems.value = false
                }
            }
        }
    }

    fun clearSearchHistory() {
        isRemovingItems.value = true

        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryDao.deleteAll()

            withContext(Dispatchers.Main) {
                isRemovingItems.value = false
            }
        }
    }
}