package com.flamyoad.tsukiviewer.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs: SharedPreferences

    private val packageAppList = MutableLiveData<List<ResolveInfo>>()
    fun packageAppList(): LiveData<List<ResolveInfo>> = packageAppList

    init {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
        }
        val pkgAppList: List<ResolveInfo> = application.packageManager.queryIntentActivities(intent, 0)
        packageAppList.value = pkgAppList

        prefs = application.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    fun setThirdPartyGallery(packageName: String) {
        prefs.edit()
            .putString(MainPreferences.EXTERNAL_GALLERY_PKG_NAME, packageName)
            .apply()
    }
}