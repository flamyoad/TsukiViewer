package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import android.widget.GridView
import androidx.core.content.ContentResolverCompat
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.adapter.CollectionListAdapter
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.model.Collection
import com.flamyoad.tsukiviewer.model.CollectionWithCriterias
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.repository.CollectionRepository
import com.flamyoad.tsukiviewer.ui.doujinpage.GridViewStyle
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class CollectionViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db: AppDatabase
    private val pathDao: IncludedPathDao
    private val contentResolver: ContentResolver = app.contentResolver
    private val myAppPreference = MyAppPreference.getInstance(app.applicationContext)

    private val collectionRepo: CollectionRepository

    private var initThumbnailJob: Job? = null

    private val searchQuery = MutableLiveData<String>("")

    private val collectionViewStyle = MutableLiveData<Int>()
    fun collectionViewStyle(): LiveData<Int> = collectionViewStyle.distinctUntilChanged()

    val collectionWithCriterias: LiveData<List<CollectionWithCriterias>>

    init {
        db = AppDatabase.getInstance(app.applicationContext)
        pathDao = db.includedFolderDao()

        collectionViewStyle.value = myAppPreference.getCollectionViewStyle()

        collectionRepo = CollectionRepository(app.applicationContext)

        collectionWithCriterias = searchQuery.switchMap { query ->
            collectionRepo.getAllWithCriterias(query)
        }

        initCollectionThumbnail()
    }

    fun deleteCollection(collection: Collection) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.delete(collection.id ?: -1)
        }
    }

    fun initCollectionThumbnail() {
        initThumbnailJob?.cancel()

        initThumbnailJob = viewModelScope.launch(Dispatchers.IO) {
            for (collection in collectionRepo.getAll()) {
                if (collection.coverPhoto == File("")) {
                    val thumbnail = getCollectionThumbnail(collection.id ?: -1)
                    collectionRepo.updateThumbnail(collection.id, thumbnail)
                }
            }
        }
    }

    fun filterList(query: String) {
        searchQuery.value = query
    }

    private suspend fun getCollectionThumbnail(collectionId: Long): File {
        val collection = collectionRepo.get(collectionId)

        val titleKeywords = collectionRepo.getTitles(collectionId)
        val includedTags = collectionRepo.getIncludedTags(collectionId)
        val excludedTags = collectionRepo.getExcludedTags(collectionId)

        var thumbnail = getThumbnailFromDb(
            includedTags, collection.mustHaveAllIncludedTags,
            excludedTags, collection.mustHaveAllExcludedTags
        )

        if (thumbnail != File("")) {
            return thumbnail
        }

        // If tags are not specified in the query, then we have to search from file explorer too
        if (includedTags.isEmpty() && excludedTags.isEmpty()) {
            val existingList = (app as MyApplication).fullDoujinList
            if (existingList != null) {
                thumbnail = getThumbnailFromExistingList(existingList.toList(), titleKeywords)
            } else {
                thumbnail = getThumbnailFromFileExplorer(titleKeywords)
            }
        }

        return thumbnail
    }

    private suspend fun getThumbnailFromFileExplorer(titleFilters: List<String>): File {
        val includedDirs = pathDao.getAllBlocking()
        for (dir in includedDirs) {
            val pathName = dir.toString()

            val uri = MediaStore.Files.getContentUri("external")

            val projection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.PARENT
            )

            var selection = "${MediaStore.Files.FileColumns.DATA} LIKE ?"

            for (title in titleFilters) {
                selection += " AND " + "${MediaStore.Files.FileColumns.TITLE} LIKE ?"
            }

            val keywordArray = titleFilters.map { title -> "%" + title + "%" }
                .toTypedArray()

            val params = arrayOf("%" + pathName + "%", *keywordArray)

            val cursor = ContentResolverCompat.query(
                contentResolver,
                uri,
                projection,
                selection,
                params,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val fullPath =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))

                val doujinDir = File(fullPath)

                val imageList = doujinDir.listFiles(ImageFileFilter())

                if (!imageList.isNullOrEmpty()) {
                    return imageList.firstOrNull()
                        ?: throw IllegalArgumentException("Could not find a picture for collection from File Explorer")
                }
            }
        }
//        return File("")
        throw IllegalArgumentException("Could not find a picture for collection from File Explorer")
    }

    private suspend fun getThumbnailFromDb(
        includedTags: List<Tag>, mustHaveAllIncludedTags: Boolean,
        excludedTags: List<Tag>, mustHaveAllExcludedTags: Boolean
    ): File {
        val doujinList = when {
            mustHaveAllIncludedTags && mustHaveAllExcludedTags -> collectionRepo.searchIncludedAndExcludedAnd(
                includedTags,
                excludedTags
            )

            !mustHaveAllIncludedTags && mustHaveAllExcludedTags -> collectionRepo.searchIncludedOrExcludedAnd(
                includedTags,
                excludedTags
            )

            mustHaveAllIncludedTags && !mustHaveAllExcludedTags -> collectionRepo.searchIncludedAndExcludedOr(
                includedTags,
                excludedTags
            )

            else -> collectionRepo.searchIncludedOrExcludedOr(includedTags, excludedTags)
        }

        val doujin = doujinList.firstOrNull()?.absolutePath ?: File("")
        val doujinImages = doujin.listFiles(ImageFileFilter())
        if (!doujinImages.isNullOrEmpty()) {
            return doujinImages.firstOrNull() ?: File("")
        }
        return File("")
    }

    private fun getThumbnailFromExistingList(doujinList: List<Doujin>, keywordList: List<String>): File {
        for (doujin in doujinList) {
            if (doujin.containsKeyTitle(keywordList)) {
                val doujinImages = doujin.path.listFiles(ImageFileFilter())
                return doujinImages?.firstOrNull() ?: File("")
            }
        }
        return File("")
    }

    private fun Doujin.containsKeyTitle(keywordList: List<String>): Boolean {
        val loweredCaseTitle = this.title.toLowerCase(Locale.ROOT)
        for (keyword in keywordList) {
            if (!loweredCaseTitle.contains(keyword)) {
                return false
            }
        }
        return true
    }

    fun switchViewStyle(viewStyle: Int) {
        myAppPreference.setCollectionViewStyle(viewStyle)
        collectionViewStyle.value = viewStyle
    }
}