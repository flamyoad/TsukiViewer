package com.flamyoad.tsukiviewer.ui.reader

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import com.flamyoad.tsukiviewer.utils.WindowsExplorerComparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*


class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val imageList = MutableLiveData<List<File>>()
    fun imageList(): LiveData<List<File>> = imageList

    private val bottomThumbnailSelectedItem = MutableLiveData(-1)
    fun bottomThumbnailSelectedItem(): LiveData<Int> = bottomThumbnailSelectedItem

    private val directoryNoLongerExists = MutableLiveData<Boolean>(false)
    fun directoryNoLongerExists(): LiveData<Boolean> = directoryNoLongerExists

    private var shouldUseWindowsSort: Boolean = false

    var currentImagePosition: Int = 0

    var currentPath: String = ""

    var shouldScrollWithVolumeButton: Boolean
        private set

    var scrollingMode: VolumeButtonScrollMode = VolumeButtonScrollMode.Nothing
        private set

    var volumeUpAction: VolumeButtonScrollDirection = VolumeButtonScrollDirection.Nothing
        private set

    var volumeDownAction: VolumeButtonScrollDirection = VolumeButtonScrollDirection.Nothing
        private set

    var scrollDistance: Int = 0
        private set

    private val readerMode = MutableLiveData<ReaderMode>()
    fun readerMode(): LiveData<ReaderMode> = readerMode.distinctUntilChanged()

    init {
        val prefs = MyAppPreference
            .getInstance(application.applicationContext)

        val defaultReaderMode = prefs.getDefaultReaderMode()
        readerMode.value = defaultReaderMode

        shouldScrollWithVolumeButton = prefs.shouldScrollWithVolumeButton()

        if (shouldScrollWithVolumeButton) {
            scrollingMode = prefs.getVolumeButtonScrollMode()
            volumeUpAction = prefs.getVolumeUpAction()
            volumeDownAction = prefs.getVolumeDownAction()
            scrollDistance = prefs.getVolumeButtonScrollDistance()
        }
    }

    fun scanForImages(dirPath: String) {
        if (dirPath == currentPath) {
            return
        }
        val dir = File(dirPath)

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // If fetchedImages is null means directory has been renamed or deleted
                val fetchedImages = dir.listFiles(ImageFileFilter()) ?: return@withContext

                Arrays.sort(fetchedImages, object : Comparator<File> {
                    private val NATURAL_SORT: Comparator<String> =
                        WindowsExplorerComparator()

                    override fun compare(o1: File, o2: File): Int {
                        return NATURAL_SORT.compare(o1.name, o2.name)
                    }
                })

                withContext(Dispatchers.Main) {
                    imageList.value = fetchedImages.toList()
                }
            }
        }
    }

    fun setReaderMode(mode: ReaderMode) {
        readerMode.value = mode
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