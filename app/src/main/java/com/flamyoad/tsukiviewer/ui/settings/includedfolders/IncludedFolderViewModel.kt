package com.flamyoad.tsukiviewer.ui.settings.includedfolders

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.model.IncludedPath
import kotlinx.coroutines.launch
import java.io.File

class IncludedFolderViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase

    private val pathDao: IncludedPathDao

    val pathList: LiveData<List<IncludedPath>>

    init {
        db = AppDatabase.getInstance(application)
        pathDao = db.includedFolderDao()
        pathList = pathDao.getAll()
    }

    fun insert(dir: File) {
        Log.d("files", dir.canonicalFile.toString())
        val folder = IncludedPath(dir = dir.canonicalFile)
        viewModelScope.launch {
            pathDao.insert(folder)
        }
    }

    fun delete(includedPath: IncludedPath) {
        viewModelScope.launch {
            pathDao.delete(includedPath)
        }
    }
}