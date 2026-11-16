package com.flamyoad.tsukiviewer.ui.settings.includedfolders

import com.flamyoad.tsukiviewer.core.repository.RelatedMangaRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.core.model.IncludedPath
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class IncludedFolderViewModel @Inject constructor(
    private val pathDao: IncludedPathDao
) : ViewModel() {

    val pathList: LiveData<List<IncludedPath>> = pathDao.getAll()

    fun insert(dir: File) {
        Log.d("files", dir.canonicalFile.toString())
        val folder = IncludedPath(dir = dir.canonicalFile)
        viewModelScope.launch {
            pathDao.insert(folder)
            RelatedMangaRepository.invalidateLibrary()
        }
    }

    fun delete(includedPath: IncludedPath) {
        viewModelScope.launch {
            pathDao.delete(includedPath)
            RelatedMangaRepository.invalidateLibrary()
        }
    }
}