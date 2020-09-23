package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Application
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class LocalDoujinViewModel(private val app: Application) : AndroidViewModel(app) {

    val repo = MetadataRepository(app)

    private val contentResolver: ContentResolver

    private var doujinListBuffer = mutableListOf<Doujin>()

    private var doujinList = MutableLiveData<MutableList<Doujin>>()
    fun doujinList(): LiveData<MutableList<Doujin>> = doujinList

    private val isLoading = MutableLiveData<Boolean>(false)
    fun isLoading(): LiveData<Boolean> = isLoading

    private val isSorting = MutableLiveData<Boolean>(false)
    fun isSorting(): LiveData<Boolean> = isSorting

    private val toastText = MutableLiveData<String?>(null)
    fun toastText(): LiveData<String?> = toastText

    private val sortMode = MutableLiveData<DoujinSortingMode>(DoujinSortingMode.NONE)

    fun sortMode(): LiveData<DoujinSortingMode> = Transformations
        .distinctUntilChanged(sortMode)

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    private var fetchService: FetchMetadataService? = null

    private var serviceConnection: ServiceConnection? = null


    init {
        contentResolver = app.contentResolver
        initDoujinList()
    }

    private fun initDoujinList() {
        val fullList = (app as MyApplication).fullDoujinList

        if (fullList != null) {
//            doujinListBuffer = fullList
            doujinList.value = fullList
        } else {
            doujinListBuffer.clear()
            fetchDoujinsFromDir()
        }
    }

    private suspend fun setResult(doujin: Doujin) {
        withContext(Dispatchers.Main) {
            doujinListBuffer.add(doujin)
            doujinList.value = doujinListBuffer

            fetchService?.enqueue(doujin.path, onlyOneItem = false)
        }
    }

    private fun fetchDoujinsFromDir() {
        viewModelScope.launch {
            isLoading.value = true

            withContext(Dispatchers.IO) {
                val includedPaths = repo.pathDao.getAllBlocking()
                for (folder in includedPaths) {
                    walk(folder.dir, folder.dir)
                }
            }

            (app as MyApplication).fullDoujinList = doujinListBuffer
            isLoading.value = false

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

                val doujin = Doujin(coverImage, title, numberOfImages, lastModified, currentDir)
                setResult(doujin)
            }

            for (f in fileList) {
                walk(f, parentDir)
            }
        }
    }

    suspend fun fetchMetadataAll(dirList: List<File>) {
        val amountFetched = repo.fetchMetadataAll(dirList)

        if (amountFetched == 0) {
            toastText.value = "All items are already synced"

        } else {
            val noun = if (amountFetched > 1)
                "items"
            else
                "item"

            toastText.value = "Sync done for $amountFetched $noun"
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
    }

    fun startSorting() {
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
                    }
                }

                doujinList.value = doujinListBuffer
                isSorting.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (serviceConnection != null) {
            app.unbindService(serviceConnection)
        }
    }

}
