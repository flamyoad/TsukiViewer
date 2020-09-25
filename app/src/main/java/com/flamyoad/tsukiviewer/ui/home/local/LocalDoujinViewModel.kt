package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Application
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class LocalDoujinViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repo = MetadataRepository(app)

    private val contentResolver: ContentResolver = app.contentResolver

    private var doujinListBuffer = mutableListOf<Doujin>()

    private var doujinList = MutableLiveData<MutableList<Doujin>>()
    fun doujinList(): LiveData<MutableList<Doujin>> = doujinList

    val includedDirectories: LiveData<List<IncludedPath>>

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
        includedDirectories = repo.pathDao.getAll().distinctUntilChanged()
        initDoujinList()
    }

    private fun initDoujinList() {
        val fullList = (app as MyApplication).fullDoujinList

        if (fullList != null) {
//            doujinListBuffer = fullList
            doujinList.value = fullList
        } else {
            doujinListBuffer.clear()
            loadDoujinsFromDir()
        }
    }

    fun reloadDoujins() {
        loadingJob?.cancel()
        loadDoujinsFromDir()
    }

    private fun loadDoujinsFromDir() {
        loadingJob = viewModelScope.launch {
            isLoading.value = true

            val prevListSize = doujinListBuffer.size

            withContext(Dispatchers.IO) {
                val includedDirs = repo.pathDao.getAllBlocking()

                // Clears doujins from directories previously included, but are now removed by the user
                if (doujinListBuffer.isNotEmpty()) {
                    val filteredList = doujinListBuffer
                        .filter { x -> x.parentDir in includedDirs }
                        .toMutableList()

                    withContext(Dispatchers.Main) {
                        doujinListBuffer = filteredList
                        doujinList.value = filteredList
                    }
                }

                // Re-fetch a new list from the included directories
                for (dir in includedDirs) {
                    walk(dir, dir)
                }
            }

            (app as MyApplication).fullDoujinList = doujinListBuffer
            isLoading.value = false

            val newListSize = doujinListBuffer.size
            postRefreshResult(newListSize - prevListSize)

            fetchService?.ongoingQueue = false
        }
    }

    // Recursive method to search for directories & sub-directories
    private suspend fun walk(currentDir: File, parentDir: File) {
        if (currentDir.isDirectory) {
            val fileList = currentDir.listFiles()

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
                    parentDir = parentDir
                )

                setResult(doujin, parentDir)
            }

            for (f in fileList) {
                walk(f, parentDir)
            }
        }
    }

    private suspend fun setResult(doujin: Doujin, parentDir: File) {
        withContext(Dispatchers.Default) {
            val item = doujinListBuffer.find { x -> x.path == doujin.path }

            if (item != null) {
                item.parentDir = parentDir
            } else {
                doujinListBuffer.add(doujin)
            }

            withContext(Dispatchers.Main) {
                doujinList.value = doujinListBuffer
                fetchService?.enqueue(doujin.path, onlyOneItem = false)
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

//                java.util.ConcurrentModificationException
//                at java.util.ArrayList$Itr.next(ArrayList.java:831)
                val tempList = doujinListBuffer.toMutableList()

                viewModelScope.launch(Dispatchers.Default) {
                    val dirs = tempList.map { doujin -> doujin.path }

                    withContext(Dispatchers.Main) {
                        fetchService?.enqueue(dirs)
                        fetchService?.startFetching()
                    }
                }
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                Log.d("fetchService", "onServiceDisconnected() is called")
                fetchService = null
            }
        }

        val bindIntent = Intent(context, FetchMetadataService::class.java)
        context.bindService(bindIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)
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
