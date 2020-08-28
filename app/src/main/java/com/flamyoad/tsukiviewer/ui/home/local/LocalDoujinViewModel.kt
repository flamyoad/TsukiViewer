package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class LocalDoujinViewModel(application: Application) : AndroidViewModel(application) {

    val repo = MetadataRepository(application)

    val includedPathList: LiveData<List<IncludedPath>>

    private var doujinList = MutableLiveData<MutableList<Doujin>>()

    private val isSyncing = MutableLiveData<Boolean>(false)

    private val toastText = MutableLiveData<String?>(null)

    private val imageExtensions = listOf("jpg", "png", "gif", "jpeg")

    fun doujinList(): LiveData<MutableList<Doujin>> = doujinList

    fun isSyncing(): LiveData<Boolean> = isSyncing

    fun toastText(): LiveData<String?> = toastText

    init {
        includedPathList = repo.pathDao.getAll()
    }

    fun fetchDoujinsFromDir(includedPaths: List<IncludedPath>) {
        viewModelScope.launch {
            isSyncing.value = true

            withContext(Dispatchers.IO) {
                val doujinList = mutableListOf<Doujin>()
                for (folder in includedPaths) {
                    walk(folder.dir, folder.dir, doujinList)
                }
            }

            isSyncing.value = false
        }
    }

    // Recursive method to search for directories & sub-directories
    private suspend fun walk(currentDir: File, parentDir: File, tempList: MutableList<Doujin>) {
        if (currentDir.isDirectory) {
            val fileList = currentDir.listFiles()

            val imageList = fileList.filter { f -> f.extension in imageExtensions }

            if (imageList.isNotEmpty()) {

                val coverImage = imageList.first().toUri()
                val title = currentDir.name
                val numberOfImages = imageList.size
                val lastModified = currentDir.lastModified()

                tempList.add(
                    Doujin(coverImage, title, numberOfImages, lastModified, currentDir)
                )

                this.doujinList.postValue(tempList)
            }

            for (f in fileList) {
                walk(f, parentDir, tempList)
            }
        }
    }

    suspend fun fetchMetadataAll(dirList: List<File>) {
        val amountFetched = repo.fetchMetadataAll(dirList)

        if (amountFetched == 0) {
            toastText.value = "All items are already synced"

        } else {
            val noun = if (amountFetched > 1)
                "items"
            else
                "item"

            toastText.value = "Sync done for $amountFetched $noun"
        }
    }

}
