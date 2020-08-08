package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedFolder
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import java.io.File


class LocalDoujinViewModel(application: Application) : AndroidViewModel(application) {

    val repo = MetadataRepository(application)

    val includedFolderList: LiveData<List<IncludedFolder>>

    val doujinList: LiveData<List<Doujin>>

    private val _isSyncing = MutableLiveData<Boolean>(false)
    val isSyncing: LiveData<Boolean> = _isSyncing

    private val _toastText = MutableLiveData<String?>(null)
    val toastText: LiveData<String?> = _toastText

    init {
        includedFolderList = repo.folderDao.getAll()

        doujinList =
            Transformations.map(includedFolderList) { folders ->
                return@map fetchDoujinsFromDir(folders)
            }
    }

    private fun fetchDoujinsFromDir(includedFolders: List<IncludedFolder>): List<Doujin> {
        val doujinList = mutableListOf<Doujin>()
        for (folder in includedFolders) {
            walk(folder.dir, doujinList)
        }
        return doujinList
    }

    // Recursive method to search for directories & sub-directories
    // todo: this method doesnt include the main directory itself, fix it
    private fun walk(currentDir: File, doujinList: MutableList<Doujin>) {
        for (f in currentDir.listFiles()) {
            if (f.isDirectory) {
                val imageList = f.listFiles(ImageFileFilter()).sorted()

                if (imageList.isNotEmpty()) {
                    val coverImage = imageList.first().toUri()
                    val title = f.name
                    val numberOfImages = imageList.size
                    val lastModified = f.lastModified()

                    doujinList.add(
                        Doujin(coverImage, title, numberOfImages, lastModified, f)
                    )
                }

                walk(f, doujinList)
            }
        }
    }

    suspend fun fetchMetadataAll(dirList: List<File>) {
        _isSyncing.value = true

        val amountFetched = repo.fetchMetadataAll(dirList)

        if (amountFetched == 0) {
            _toastText.value = "All items are already synced"

        } else {
            val noun = if (amountFetched > 1)
                "items"
            else
                "item"

            _toastText.value = "Sync done for $amountFetched $noun"
        }

        _isSyncing.value = false
    }

}
