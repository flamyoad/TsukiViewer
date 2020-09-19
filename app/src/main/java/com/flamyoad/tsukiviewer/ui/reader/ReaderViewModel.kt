package com.flamyoad.tsukiviewer.ui.reader

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val imageList = MutableLiveData<List<File>>()

    fun imageList(): LiveData<List<File>> = imageList

//    var totalImageCount: Int = -1
//        private set
    private val totalImageCount = MutableLiveData<Int>(-1)

    fun totalImageCount(): LiveData<Int> = totalImageCount

    var currentPath: String = ""

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
                    totalImageCount.value = fetchedImages.size
                    imageList.value = fetchedImages
                }
            }
        }

    }

}