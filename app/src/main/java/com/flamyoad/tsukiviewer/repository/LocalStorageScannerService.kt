package com.flamyoad.tsukiviewer.repository

import androidx.core.net.toUri
import com.flamyoad.tsukiviewer.core.DispatcherProvider
import com.flamyoad.tsukiviewer.model.Doujin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import javax.inject.Inject


interface LocalStorageScannerService {
    fun getDoujins(): Flow<List<Doujin>>
}

@ExperimentalCoroutinesApi
class LocalStorageScannerServiceImpl @Inject constructor(
    private val directoryManager: DirectoryManager,
    private val dispatcherProvider: DispatcherProvider,
) : LocalStorageScannerService {

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    // not thread safe
    private val listToBePopulated = mutableListOf<Doujin>()

    override fun getDoujins(): Flow<List<Doujin>> = channelFlow {
        for (directory in directoryManager.getIncludedDirectories()) {
            walk(directory, directory, ::send)
        }
    }

    /**
     * Recursive method to search for directories & sub-directories
     */
    private suspend fun walk(
        currentDir: File,
        parentDir: File,
        callback: suspend (List<Doujin>) -> Unit
    ) {
        if (currentDir.isDirectory) {
            val fileList = currentDir.listFiles() ?: return
            val imageList = fileList.filter { f -> f.extension in imageExtensions }

            if (imageList.isNotEmpty()) {

                val coverImage = imageList.first().toUri()
                val title = currentDir.name
                val numberOfImages = imageList.size
                val lastModified = currentDir.lastModified()

                val doujin = Doujin(
                    coverImage,
                    title,
                    numberOfImages,
                    lastModified,
                    currentDir,
                    parentDir
                )
                /*
                    If the same folder is found twice during the recursive scanning, the new item is not added to the list.
                    Instead, the parent directory of the item is changed to make it easier to be removed, when the included directories are modified by user
                */
                val item = listToBePopulated.find { x -> x.path == doujin.path }

                if (item != null) {
                    item.parentDir = parentDir
                } else {
                    listToBePopulated.add(doujin)
                }
                callback(listToBePopulated)
            }

            for (f in fileList) {
                walk(f, parentDir, callback)
            }
        }
    }

}