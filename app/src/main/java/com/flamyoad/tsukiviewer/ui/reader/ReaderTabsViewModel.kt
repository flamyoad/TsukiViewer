package com.flamyoad.tsukiviewer.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flamyoad.tsukiviewer.model.ReaderTabHistory

class ReaderTabsViewModel(application: Application) : AndroidViewModel(application) {

    val tabList: LiveData<List<ReaderTabHistory>>

    init {
        val list = mutableListOf<ReaderTabHistory>()
        for (i in 0..5) {
            list.add(ReaderTabHistory(i.toLong(), "Ayumu chan"))
        }
        tabList = MutableLiveData<List<ReaderTabHistory>>(list)
    }
}