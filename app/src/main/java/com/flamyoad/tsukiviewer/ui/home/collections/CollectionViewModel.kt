package com.flamyoad.tsukiviewer.ui.home.collections

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContentResolverCompat
import androidx.core.net.toFile
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao
import com.flamyoad.tsukiviewer.core.model.*
import com.flamyoad.tsukiviewer.core.model.Collection
import com.flamyoad.tsukiviewer.core.repository.CollectionRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import com.flamyoad.tsukiviewer.utils.extensions.toDoujin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

class CollectionViewModel @Inject constructor(
    private val application: Application,
    private val pathDao: IncludedPathDao,
    private val collectionRepo: CollectionRepository
) : ViewModel() {
    private val contentResolver: ContentResolver = application.contentResolver
    private val myAppPreference = MyAppPreference.getInstance(application.applicationContext)

    private var initThumbnailJob: Job? = null

    private val searchQuery = MutableLiveData<String>("")

    private val collectionViewStyle = MutableLiveData<Int>()
    fun collectionViewStyle(): LiveData<Int> = collectionViewStyle.distinctUntilChanged()

    val collectionWithCriterias: LiveData<List<CollectionWithCriterias>>

    init {
        collectionViewStyle.value = myAppPreference.getCollectionViewStyle()

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
        val lowerCasedTitleKeywords = titleKeywords.map { title -> title.toLowerCase(Locale.ROOT) }

        val includedTags = collectionRepo.getIncludedTags(collectionId)
        val excludedTags = collectionRepo.getExcludedTags(collectionId)

        // If dirs from collection is empty, means all paths are included. Else, just search from the collection's specified dirs
        val dirs = collectionRepo.getDirectories(collectionId)
        val includedDirs = when (dirs.isEmpty()) {
            true -> pathDao.getAllBlocking()
            false -> dirs
        }

        val collectionSearchInput = CollectionSearchInput(
            collection,
            lowerCasedTitleKeywords,
            includedTags,
            excludedTags,
            collection.minNumPages,
            collection.maxNumPages,
            includedDirs
        )

        var thumbnail = getThumbnailFromDb(collectionSearchInput)

        if (thumbnail != File("")) {
            return thumbnail
        }

        // If tags are not specified in the query, then we have to search from file explorer too
        if (includedTags.isEmpty() && excludedTags.isEmpty()) {
            val existingList = (application as MyApplication).fullDoujinList
            if (existingList != null) {
                thumbnail =
                    getThumbnailFromExistingList(existingList.toList(), collectionSearchInput)
            } else {
                thumbnail = getThumbnailFromFileExplorer(collectionSearchInput)
            }
        }

        return thumbnail
    }

    private fun getThumbnailFromFileExplorer(input: CollectionSearchInput): File {
        for (dir in input.mustHaveDirs) {
            val pathName = dir.toString()

            val uri = MediaStore.Files.getContentUri("external")

            val projection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.PARENT
            )

            var selection = "${MediaStore.Files.FileColumns.DATA} LIKE ?"

            for (title in input.titleKeywords) {
                selection += " AND " + "${MediaStore.Files.FileColumns.TITLE} LIKE ?"
            }

            val keywordArray = input.titleKeywords.map { title -> "%" + title + "%" }
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

            while (cursor?.moveToNext() == true) {
                val fullPath =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))

                val dir = File(fullPath)

                val imageList = dir.listFiles(ImageFileFilter())
                if (imageList.isNullOrEmpty())
                    continue

                val doujin = dir.toDoujin() ?: continue

                if (doujin.hasFulfilledCriteria(input)) {
                    return doujin.pic.toFile()
                }
            }
        }
        return File("")
    }

    private suspend fun getThumbnailFromDb(input: CollectionSearchInput): File {
        val doujinDetailList: List<DoujinDetails>
        with(input) {
            val mustHaveAllIncludedTags = collection.mustHaveAllIncludedTags
            val mustHaveAllExcludedTags = collection.mustHaveAllExcludedTags

            doujinDetailList = when {
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
        }

        Log.d("debugs", doujinDetailList.size.toString())
        for (detail in doujinDetailList) {
            val doujin = detail.absolutePath.toDoujin() ?: continue

            if (doujin.hasFulfilledCriteria(input)) {
                return doujin.pic.toFile()
            }
        }

        return File("")
    }

    private fun getThumbnailFromExistingList(
        doujinList: List<Doujin>,
        input: CollectionSearchInput
    ): File {
        for (doujin in doujinList) {
            if (doujin.hasFulfilledCriteria(input)) {
                return doujin.pic.toFile()
            }
        }
        return File("")
    }

    private fun Doujin.hasFulfilledCriteria(input: CollectionSearchInput): Boolean {
        if (this.numberOfItems < input.minNumberPages || this.numberOfItems > input.maxNumberPages) {
            return false
        }

        for (keyword in input.titleKeywords) {
            val loweredCaseTitle = this.title.toLowerCase(Locale.ROOT)
            if (!loweredCaseTitle.contains(keyword)) {
                return false
            }
        }

        val mustHaveParentDirs = input.mustHaveDirs
        if (mustHaveParentDirs.isNotEmpty()) {
            for (parent in mustHaveParentDirs) {
                val itemPath = this.path.canonicalPath
                val parentPath = parent.canonicalPath

                if (itemPath.contains(parentPath)) {
                    return true
                }
            }
            return false
        }

        return true
    }

    fun switchViewStyle(viewStyle: Int) {
        myAppPreference.setCollectionViewStyle(viewStyle)
        collectionViewStyle.value = viewStyle
    }
}