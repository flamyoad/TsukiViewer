package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.core.model.Source
import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import com.flamyoad.tsukiviewer.utils.ImageFileFilter
import com.flamyoad.tsukiviewer.utils.WindowsExplorerComparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject

class DoujinViewModel @Inject constructor(
    private val application: Application,
    private val metadataRepo: MetadataRepository,
    private val bookmarkRepo: BookmarkRepository
) : ViewModel() {

    private val myAppPreference = MyAppPreference.getInstance(application.applicationContext)

    lateinit var detailWithTags: LiveData<DoujinDetailsWithTags>

    private val imageList = MutableLiveData<List<File>>()
    fun imageList(): LiveData<List<File>> = imageList

    private val coverImage = MutableLiveData<Uri>()
    fun coverImage(): LiveData<Uri> = coverImage

    private val bookmarkGroupList = MutableLiveData<List<BookmarkGroup>>()
    fun bookmarkGroupList(): LiveData<List<BookmarkGroup>> = bookmarkGroupList

    private val directoryNoLongerExists = MutableLiveData<Boolean>(false)
    fun directoryNoLongerExists(): LiveData<Boolean> = directoryNoLongerExists

    private val gridViewStyle = MutableLiveData<GridViewStyle>()
    fun gridViewStyle(): LiveData<GridViewStyle> = gridViewStyle

    private val landingPage = MutableLiveData<LandingPageMode>()
    fun landingPage(): LiveData<LandingPageMode> = landingPage

    private var shouldUseWindowsSort: Boolean = false

    val newCollectionName = MutableLiveData<String>()

    val collectionNameExists: LiveData<Boolean> = newCollectionName.switchMap { name ->
        return@switchMap bookmarkRepo.groupNameExists(name)
    }

    val bookmarkGroupTickStatus = hashMapOf<String, Boolean>()

    val snackbarText = MutableLiveData<String>("")

    var currentPath: String = ""

    init {
        gridViewStyle.value = myAppPreference.getDefaultViewStyle()
        landingPage.value = myAppPreference.getDefaultLandingPage()
        shouldUseWindowsSort = myAppPreference.shouldUseWindowsSort()
    }

    fun scanForImages(dirPath: String) {
        if (dirPath == currentPath) {
            return
        } else {
            currentPath = dirPath
        }

        val dir = File(dirPath)

        detailWithTags = metadataRepo
            .doujinDetailsDao
            .getLongDetailsByPath(dir.toString())

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val fetchedImages = dir.listFiles(ImageFileFilter())

                /*
                    sortedBy { x -> x.name } will return the following wrong result:
                    ['0.jpg', '1.jpg', '10.jpg', '11.jpg', '12.jpg' . . .]

                    This is because sortedBy() is using ASCII order to sort.
                    Workaround is to use Natural Sort

                    Article describing the differences about programming language's in-built sort and natural sort
                    https://blog.codinghorror.com/sorting-for-humans-natural-sort-order/

                    Sort filenames in directory in ascending order [duplicate]
                    https://stackoverflow.com/questions/33159106/sort-filenames-in-directory-in-ascending-order
                */
                if (fetchedImages == null) {
                    withContext(Dispatchers.Main) {
                        directoryNoLongerExists.value = true
                    }
                    return@withContext
                }

                val sortedImages: List<File>
                when (shouldUseWindowsSort) {
                    true -> {
                        Arrays.sort(fetchedImages, object : Comparator<File> {
                            private val NATURAL_SORT: Comparator<String> =
                                WindowsExplorerComparator()

                            override fun compare(o1: File, o2: File): Int {
                                return NATURAL_SORT.compare(o1.name, o2.name)
                            }
                        })
                        sortedImages = fetchedImages.toList()
                    }

                    false -> {
                        val naturalSort = compareBy<File> { it.name.length }
                            .then(naturalOrder())
                        sortedImages = fetchedImages.sortedWith(naturalSort)
                    }
                }


                withContext(Dispatchers.Main) {
                    imageList.value = sortedImages.toList()

                    val firstImage = sortedImages.first().toUri()
                    coverImage.value = firstImage
                }
            }
        }
    }

    fun detailsNotExists(): Boolean {
        if (!this::detailWithTags.isInitialized)
            return true

        return (detailWithTags.value == null)
    }

    fun resetTags(sources: EnumSet<Source>) {
        val dir = File(currentPath)
        viewModelScope.launch(Dispatchers.IO) {
            metadataRepo.resetTags(dir, sources)
        }
    }

    fun removeMetadata() {
        val data = detailWithTags.value?.doujinDetails

        if (data != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val doujinDetails =
                        metadataRepo.removeMetadata(data)
                }
            }
        } else {
            snackbarText.value = "Failed to remove existing title & tags"
            snackbarText.value = "" // Clears the value
        }
    }

    fun initCollectionList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (currentPath.isNotBlank()) {
                val dir = File(currentPath)
                val collections = bookmarkRepo.getAllGroupsFrom(dir)

                withContext(Dispatchers.Main) {
                    bookmarkGroupList.value = collections
                }
            }
        }
    }

    fun insertItemIntoTickedCollections() {
        viewModelScope.launch(Dispatchers.IO) {
            val status = bookmarkRepo.wipeAndInsertNew(File(currentPath), bookmarkGroupTickStatus)

            withContext(Dispatchers.Main) {
                snackbarText.value = status
            }
        }
    }

    fun fetchBookmarkGroup() {
        bookmarkGroupTickStatus.clear()

        val currentDir = File(currentPath)

        viewModelScope.launch(Dispatchers.IO) {
            val tickedBookmarkGroups = bookmarkRepo.getAllGroupsFrom(currentDir)
            withContext(Dispatchers.Main) {
                for (group in tickedBookmarkGroups) {
                    if (group.isTicked) {
                        bookmarkGroupTickStatus.put(group.name, true)
                    }
                }
                bookmarkGroupList.value = tickedBookmarkGroups
            }
        }
    }


    fun createCollection(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepo.insertGroup(BookmarkGroup(name))
        }
    }

    fun getNukeCode(): String? {
        val nukeCode = detailWithTags.value?.doujinDetails?.nukeCode.toString()
        return if (nukeCode == "null") // Room actually returns a null string lul
            null
        else
            nukeCode
    }

    fun switchViewStyle(style: GridViewStyle) {
        gridViewStyle.value = style
        myAppPreference.setDefaultViewStyle(style)
    }
}