package com.flamyoad.tsukiviewer.ui.settings

import android.app.Application
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val packageAppList = MutableLiveData<List<ResolveInfo>>()

    fun packageAppList(): LiveData<List<ResolveInfo>> = packageAppList

    init {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setType("image/*")
        val pkgAppList: List<ResolveInfo> = application.packageManager.queryIntentActivities(intent, 0)

        packageAppList.value = pkgAppList
    }
}