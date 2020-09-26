package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class LocalDoujinViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repo = MetadataRepository(app)

    private var includedDirectories = listOf<File>()

    private var doujinListBuffer = mutableListOf<Doujin>()

    private var doujinList = MutableLiveData<MutableList<Doujin>>(mutableListOf())
    fun doujinList(): LiveData<MutableList<Doujin>> = doujinList

    private val isLoading = MutableLiveData<Boolean>(false)
    fun isLoading(): LiveData<Boolean> = isLoading

    private val isSorting = MutableLiveData<Boolean>(false)
    fun isSorting(): LiveData<Boolean> = isSorting

    private val toastText = MutableLiveData<String?>(null)
    fun toastText(): LiveData<String?> = toastText

    private val refreshResult = MutableLiveData<String?>(null)
    fun refreshResult(): LiveData<String?> = refreshResult

    private val sortMode = MutableLiveData<DoujinSortingMode>(DoujinSortingMode.NONE)
    fun sortMode(): LiveData<DoujinSortingMode> = sortMode.distinctUntilChanged()

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    private var fetchService: FetchMetadataService? = null

    private var serviceConnection: ServiceConnection? = null

    private var loadingJob: Job? = null

    init {
        initDoujinList()
    }

    private fun initDoujinList() {
        val fullList = (app as MyApplication).fullDoujinList

        if (fullList != null) {
            doujinListBuffer = fullList
            doujinList.value = fullList
        } else {
            doujinListBuffer.clear()
            loadDoujinsFromDir()
        }
    }

    fun reloadDoujins() {
        viewModelScope.launch(Dispatchers.IO) {
            val directoriesFromDb = repo.pathDao.getAllBlocking()

            if (includedDirectories == directoriesFromDb) {
                return@launch

            } else {
                loadingJob?.cancel()
                loadDoujinsFromDir()
            }
        }
    }

    private fun loadDoujinsFromDir() {
        loadingJob = viewModelScope.launch {
            isLoading.value = true

            val prevListSize = doujinListBuffer.size

            withContext(Dispatchers.IO) {
                includedDirectories = repo.pathDao.getAllBlocking()

                // Clears doujins from directories previously included, but are now removed by the user
                if (doujinListBuffer.isNotEmpty()) {
                    val filteredList = doujinListBuffer
                        .filter { x -> x.parentDir in includedDirectories }
                        .toMutableList()

                    withContext(Dispatchers.Main) {
                        doujinListBuffer = filteredList
                        doujinList.value = filteredList
                    }
                }

                // Re-fetch a new list from the included directories
                for (dir in includedDirectories) {
                    walk(dir, dir)
                }

                withContext(Dispatchers.Main) {
                    (app as MyApplication).fullDoujinList = doujinListBuffer
                    isLoading.value = false

                    val newListSize = doujinListBuffer.size
                    postRefreshResult(newListSize - prevListSize)

                    if (fetchService != null) {
                        enqueueDirs()
                    }
                }
            }
        }
    }

    // Recursive method to search for directories & sub-directories
    private suspend fun walk(currentDir: File, parentDir: File) {
        if (currentDir.isDirectory) {
            val fileList = currentDir.listFiles()

            if (fileList == null) {
                return // Invalid directory
            }

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
                doujinList.value = doujinListBuffer
            }
        }
    }

    // Todo: check which method inside is blocking UI thread
    fun fetchMetadataAll() {
        val context = app.applicationContext

        FetchMetadataService.startService(context)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                fetchService = (service as FetchMetadataService.FetchBinder).getService()

                // If all doujins have been loaded
                if (isLoading.value == false) {
                    enqueueDirs()
                }
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                fetchService = null
            }
        }

        val bindIntent = Intent(context, FetchMetadataService::class.java)
        context.bindService(bindIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    private fun enqueueDirs() {
        viewModelScope.launch(Dispatchers.Default) {
            val dirList = doujinListBuffer.map { x -> x.path }

            withContext(Dispatchers.Main) {
                fetchService?.enqueueList(dirList)
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

                doujinList.value = doujinListBuffer
                isSorting.value = false
            }
        }
    }

    suspend fun sortByShortName(ascending: Boolean): MutableList<Doujin> {
        val shortTitles = repo.doujinDetailsDao.getAllShortTitles()

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

        when (ascending) {
            true -> {
                doujinList.sortWith(naturalSort)
            }
            false -> {
                val naturalSortDesc =
                    naturalSort.then(reverseOrder()) // doesnt reverse the list lul TODO: FIX IT
                doujinList.sortWith(naturalSortDesc)
            }
        }

        return doujinList
    }

    fun refreshList() {
        if (isLoading.value == true) {
            return
        }
//        fetchDoujinsFromDir()
    }

    private fun postRefreshResult(amountNewItems: Int) {
        val message = if (amountNewItems < 0) {
            "No new items found!"
        } else if (amountNewItems > 1) {
            "${amountNewItems} items are found!"
        } else {
            "${amountNewItems} item is found!"
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (serviceConnection != null) {
            app.unbindService(serviceConnection)
        }
    }

}
