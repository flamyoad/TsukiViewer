package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.launch
import java.io.File

class DoujinViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase

    private val metadataRepo = MetadataRepository(application)

    private val doujinDetailsDao: DoujinDetailsDao

    private val _imageList = MutableLiveData<List<File>>()
    val imageList: LiveData<List<File>> = _imageList

    private val _coverImage = MutableLiveData<Uri>()
    val coverImage: LiveData<Uri> = _coverImage

    lateinit var detailWithTags: LiveData<DoujinDetailsWithTags>

    var currentPath: String = ""

    init {
        db = AppDatabase.getInstance(application)
        doujinDetailsDao = db.doujinDetailsDao()
    }

    fun scanForImages(dirPath: String) {
        if (dirPath == currentPath) {
            return
        } else {
            currentPath = dirPath
        }

        val dir = File(dirPath)

        detailWithTags = doujinDetailsDao.getLongDetailsByPath(dir.toString())

        val fetchedImages = dir.listFiles(ImageFileFilter())

        /*  sortedBy { x -> x.name } will return the following wrong result:
            ['0.jpg', '1.jpg', '10.jpg', '11.jpg', '12.jpg' . . .]

            This is because sortedBy() is using ASCII order to sort.
            Workaround is to use Natural Sort

            Article describing the details about language's in-built sort and natural sort
            https://blog.codinghorror.com/sorting-for-humans-natural-sort-order/

            Sort filenames in directory in ascending order [duplicate]
            https://stackoverflow.com/questions/33159106/sort-filenames-in-directory-in-ascending-order
         */
        val naturalSort = compareBy<File> { it.name.length } // If you don't first compare by length, it won't work
            .then(naturalOrder())

        _imageList.value = fetchedImages.sortedWith(naturalSort)

        val firstImage = fetchedImages.first().toUri()
        _coverImage.value = firstImage
    }

    fun detailsNotExists(): Boolean {
        return detailWithTags.value == null
    }

    fun resetTags() {
        val dir = File(currentPath)
        viewModelScope.launch {
            metadataRepo.resetTags(dir)
        }
    }
}