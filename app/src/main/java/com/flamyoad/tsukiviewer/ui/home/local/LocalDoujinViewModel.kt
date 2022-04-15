package com.flamyoad.tsukiviewer.ui.home.local

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.core.DispatcherProvider
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.Source
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import com.flamyoad.tsukiviewer.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.repository.LocalStorageScannerService
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LocalDoujinViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bookmarkRepository: BookmarkRepository,
    private val localStorageScannerService: LocalStorageScannerService,
    private val dispatcherProvider: DispatcherProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val metadataRepo = MetadataRepository(context)

    private val selectedDoujins = mutableListOf<Doujin>()

    private var shouldResetSelections: Boolean = false

    private var includedDirectories = listOf<File>()

    private var doujinListBuffer = mutableListOf<Doujin>()

    //    private var doujinList = MutableLiveData<MutableList<Doujin>>(mutableListOf())
//    fun doujinList(): LiveData<MutableList<Doujin>> = doujinList
    fun doujinList() = localStorageScannerService.getDoujins()
        .asLiveData(dispatcherProvider.io())

    private val isLoading = MutableLiveData<Boolean>(false)
    fun isLoading(): LiveData<Boolean> = isLoading

    private val isSorting = MutableLiveData<Boolean>(false)
    fun isSorting(): LiveData<Boolean> = isSorting

    private val toastText = MutableLiveData<String?>(null)
    fun toastText(): LiveData<String?> = toastText

    private val sortMode = MutableLiveData<DoujinSortingMode>(DoujinSortingMode.NONE)
    fun sortMode(): LiveData<DoujinSortingMode> = sortMode.distinctUntilChanged()

    val newGroupName = MutableLiveData<String>()

    val groupNameIsUsed: LiveData<Boolean> = newGroupName.switchMap { name ->
        return@switchMap bookmarkRepository.groupNameExists(name)
    }

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    private var fetchService: FetchMetadataService? = null

    private var serviceConnection: ServiceConnection? = null

    private var loadingJob: Job? = null

    private var shouldSendDirsToService: Boolean = false

    private var sourceFlags: EnumSet<Source> = EnumSet.noneOf(Source::class.java)

    val snackbarText = MutableLiveData<String>("")

    val bookmarkGroupList = MutableLiveData<List<BookmarkGroup>>()

    val bookmarkGroupTickStatus = hashMapOf<String, Boolean>()

    init {
//        initDoujinList()
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkGroupList.postValue(bookmarkRepository.getAllGroupsBlocking())
        }
    }

    private fun initDoujinList() {
//        val fullList = (context as MyApplication).fullDoujinList
//
//        if (fullList != null) {
//            doujinListBuffer = fullList
//            doujinList.value = fullList
//        } else {
//            doujinListBuffer.clear()
//            loadDoujinsFromDir()
//        }
    }

    fun reloadDoujins() {
        viewModelScope.launch(Dispatchers.IO) {
            val directoriesFromDb = metadataRepo.pathDao.getAllBlocking()

            if (includedDirectories == directoriesFromDb) {
                return@launch

            } else {
                loadingJob?.cancel()
                loadDoujinsFromDir()
            }
        }
    }

    private fun loadDoujinsFromDir() {
//        loadingJob = viewModelScope.launch {
//            isLoading.value = true
//
//            withContext(Dispatchers.IO) {
//                includedDirectories = metadataRepo.pathDao.getAllBlocking()
//
//                // Clears doujins from directories previously included, but are now removed by the user
//                if (doujinListBuffer.isNotEmpty()) {
//                    val filteredList = doujinListBuffer
//                        .filter { x -> x.parentDir in includedDirectories }
//                        .toMutableList()
//
//                    withContext(Dispatchers.Main) {
//                        doujinListBuffer = filteredList
//                        doujinList.value = filteredList
//                    }
//                }
//
//                // Re-fetch a new list from the included directories
//                for (dir in includedDirectories) {
//                    walk(dir, dir)
//                }
//
//                withContext(Dispatchers.Main) {
//                    (app as MyApplication).fullDoujinList = doujinListBuffer
//                    val fullList = mutableListOf<Doujin>()
//                    isLoading.value = false
//
//                    if (shouldSendDirsToService) {
//                        enqueueDirs()
//                    }
//                }
//            }
//        }
    }


    /**
     * Recursive method to search for directories & sub-directories
     */
    private suspend fun walk(currentDir: File, parentDir: File) {
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
                setResult(doujin, parentDir)
            }

            for (f in fileList) {
                walk(f, parentDir)
            }
        }
    }

    private suspend fun setResult(doujin: Doujin, parentDir: File) {
        val list = doujinListBuffer

        withContext(Dispatchers.Default) {
            /*
            If the same folder is found twice during the recursive scanning, the new item is not added to the list.
            Instead, the parent directory of the item is changed to make it easier to be removed, when the included directories are modified by user
            */
            val item = list.find { x -> x.path == doujin.path }

            if (item != null) {
                item.parentDir = parentDir

            } else {
                doujinListBuffer.add(doujin)
            }

            withContext(Dispatchers.Main) {
                if (shouldResetSelections) {
                    for (doujinItem in doujinListBuffer) {
                        doujinItem.isSelected = false
                    }
                } else {
                    for (doujinItem in doujinListBuffer) {
                        doujinItem.isSelected = doujinItem in selectedDoujins
                    }
                }

//                doujinList.value = doujinListBuffer
                shouldResetSelections = false
            }
        }
    }

    fun fetchMetadata(sourceFlags: EnumSet<Source>) {
        FetchMetadataService.startService(context)

        this.sourceFlags = sourceFlags

        if (isLoading.value == true) {
            shouldSendDirsToService = true
        } else {
            enqueueDirs()
        }
    }

    private fun enqueueDirs() {
        val context = context.applicationContext

        viewModelScope.launch(Dispatchers.Default) {
            val dirList = doujinListBuffer.map { x -> x.path }

            withContext(Dispatchers.Main) {
                serviceConnection = object : ServiceConnection {
                    override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                        fetchService = (service as FetchMetadataService.FetchBinder).getService()

                        fetchService?.enqueueList(dirList, sourceFlags)
                        context.unbindService(this)
                    }

                    override fun onServiceDisconnected(className: ComponentName?) {
                        fetchService = null
                    }
                }

                val bindIntent = Intent(context, FetchMetadataService::class.java)
                context.bindService(bindIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun setSortMode(mode: DoujinSortingMode) {
        sortMode.value = mode
        startSorting()
    }

    private fun startSorting() {
        sortMode.value?.let {
            isSorting.value = true

            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    // Todo: Try replacing with natural sort for strings
                    when (it) {
                        DoujinSortingMode.TITLE_ASC -> doujinListBuffer.sortBy { x -> x.title }
                        DoujinSortingMode.TITLE_DESC -> doujinListBuffer.sortByDescending { x -> x.title }
                        DoujinSortingMode.DATE_ASC -> doujinListBuffer.sortBy { x -> x.lastModified }
                        DoujinSortingMode.DATE_DESC -> doujinListBuffer.sortByDescending { x -> x.lastModified }
                        DoujinSortingMode.NUM_ITEMS_ASC -> doujinListBuffer.sortBy { x -> x.numberOfItems }
                        DoujinSortingMode.NUM_ITEMS_DESC -> doujinListBuffer.sortByDescending { x -> x.numberOfItems }
                        DoujinSortingMode.PATH_ASC -> doujinListBuffer.sortBy { x -> x.path }
                        DoujinSortingMode.PATH_DESC -> doujinListBuffer.sortByDescending { x -> x.path }

                        DoujinSortingMode.SHORT_TITLE_ASC -> {
                            doujinListBuffer = sortByShortName(ascending = true)
                        }
                        DoujinSortingMode.SHORT_TITLE_DESC -> {
                            doujinListBuffer = sortByShortName(ascending = false)
                        }

                        DoujinSortingMode.NONE -> {
                        }
                    }
                }

//                doujinList.value = doujinListBuffer
                isSorting.value = false
            }
        }
    }

    suspend fun sortByShortName(ascending: Boolean): MutableList<Doujin> {
        val shortTitles = metadataRepo.doujinDetailsDao.getAllShortTitles()

        val doujinList = doujinListBuffer.toMutableList()
        for (doujin in doujinList) {
            val found = shortTitles.find { x -> x.path == doujin.path }
            if (found != null) {
                doujin.shortTitle = found.shortTitleEnglish
            } else {
                doujin.shortTitle = doujin.title
            }
        }

        val naturalSort =
            compareBy<Doujin> { it.shortTitle.length } // If you don't first compare by length, it won't work
                .then(naturalOrder())

        doujinList.sortWith(naturalSort)

        return when (ascending) {
            true -> doujinList
            false -> doujinList.asReversed()
        }
    }

    fun tickSelectedDoujin(doujin: Doujin) {
        val hasBeenSelected = selectedDoujins.contains(doujin)
        when (hasBeenSelected) {
            true -> selectedDoujins.remove(doujin)
            false -> selectedDoujins.add(doujin)
        }

        if (loadingJob?.isCompleted == true) {

            val index = doujinListBuffer.indexOf(doujin)

            val doujin = doujinListBuffer[index]
            doujin.isSelected = !hasBeenSelected

//            doujinList.value = doujinListBuffer
        }
    }

    fun clearSelectedDoujins() {
        selectedDoujins.clear()

        if (loadingJob?.isCompleted == true) {
            for (doujin in doujinListBuffer) {
                doujin.isSelected = false
            }
//            doujinList.value = doujinListBuffer

        } else {
            shouldResetSelections = true
        }
    }

    fun selectedCount(): Int {
        return selectedDoujins.size
    }

    fun getSelectedDoujins(): List<Doujin> {
        return selectedDoujins.toList()
    }

    fun insertItemIntoTickedCollections() {
        val tickedItems = bookmarkGroupTickStatus
            .filter { x -> x.value == true }
            .map { x -> x.key }

        val untickedItems = bookmarkGroupTickStatus
            .filter { x -> x.value == false }
            .map { x -> x.key }

        val bookmarkGroupsToBeAdded = tickedItems.minus(untickedItems)

        viewModelScope.launch(Dispatchers.IO) {
            val status: String
            if (selectedDoujins.size == 1) {
                val doujinPath = selectedDoujins.first().path
                status = bookmarkRepository.wipeAndInsertNew(doujinPath, bookmarkGroupTickStatus)
            } else {
                status = bookmarkRepository.insertAllItems(
                    selectedDoujins.toList(),
                    bookmarkGroupsToBeAdded
                )
            }

            withContext(Dispatchers.Main) {
                snackbarText.value = status
            }
        }
    }

    fun createCollection(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepository.insertGroup(BookmarkGroup(name))
        }
    }

    fun fetchBookmarkGroup() {
        bookmarkGroupTickStatus.clear()

        if (selectedDoujins.size == 1) {
            val doujinPath = selectedDoujins.first().path

            viewModelScope.launch(Dispatchers.IO) {
                val tickedBookmarkGroups = bookmarkRepository.getAllGroupsFrom(doujinPath)
                withContext(Dispatchers.Main) {
                    for (group in tickedBookmarkGroups) {
                        if (group.isTicked) {
                            bookmarkGroupTickStatus.put(group.name, true)
                        }
                    }
                    bookmarkGroupList.value = tickedBookmarkGroups
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val allBookmarkGroups = bookmarkRepository.getAllGroupsBlocking()
                withContext(Dispatchers.Main) {
                    bookmarkGroupList.value = allBookmarkGroups
                }
            }
        }
    }

}
