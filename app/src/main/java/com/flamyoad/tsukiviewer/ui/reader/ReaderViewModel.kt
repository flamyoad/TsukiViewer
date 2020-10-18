package com.flamyoad.tsukiviewer.ui.reader

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val imageList = MutableLiveData<List<File>>()
    fun imageList(): LiveData<List<File>> = imageList

    private val bottomThumbnailSelectedItem = MutableLiveData(-1)
    fun bottomThumbnailSelectedItem(): LiveData<Int> = bottomThumbnailSelectedItem

    var currentImagePosition: Int = 0

    var currentPath: String = ""

    var readerMode: ReaderMode = ReaderMode.VerticalStrip

    fun scanForImages(dirPath: String) {
        if (dirPath == currentPath) {
            return
        }
        val dir = File(dirPath)

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val naturalSort =
                    compareBy<File> { it.name.length } // If you don't first compare by length, it won't work
                        .then(naturalOrder())

                val fetchedImages = dir.listFiles(ImageFileFilter())
                    .sortedWith(naturalSort)

                withContext(Dispatchers.Main) {
                    imageList.value = fetchedImages
                }
            }
        }
    }

    fun onThumbnailClick(position: Int) {
        currentImagePosition = position
        bottomThumbnailSelectedItem.value = position
    }

    fun getTotalImagesCount(): Int {
        return imageList.value?.size ?: 0
    }

    fun resetBottomThumbnailState() {
        bottomThumbnailSelectedItem.value = -1
    }

}