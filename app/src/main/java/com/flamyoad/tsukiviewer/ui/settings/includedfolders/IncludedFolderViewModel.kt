package com.flamyoad.tsukiviewer.ui.settings.includedfolders

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.IncludedFolderDao
import com.flamyoad.tsukiviewer.model.IncludedFolder
import kotlinx.coroutines.launch
import java.io.File

class IncludedFolderViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase

    private val folderDao: IncludedFolderDao

    val folderList: LiveData<List<IncludedFolder>>

    init {
        db = AppDatabase.getInstance(application)
        folderDao = db.includedFolderDao()
        folderList = folderDao.getAll()
    }

    fun insert(dir: File) {
        Log.d("files", dir.canonicalFile.toString())
        val folder = IncludedFolder(dir = dir.canonicalFile)
        viewModelScope.launch {
            folderDao.insert(folder)
        }
    }

    fun delete(includedFolder: IncludedFolder) {
        viewModelScope.launch {
            folderDao.delete(includedFolder)
        }
    }
}