package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Application
import android.content.*
import android.os.IBinder
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private var tempDoujins = mutableListOf<Doujin>()

    private var doujinList = MutableLiveData<MutableList<Doujin>>()

    private val isSyncing = MutableLiveData<Boolean>(false)

    private val toastText = MutableLiveData<String?>(null)

    private val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

    private var fetchService: FetchMetadataService? = null

    fun doujinList(): LiveData<MutableList<Doujin>> = doujinList

    fun isSyncing(): LiveData<Boolean> = isSyncing

    fun toastText(): LiveData<String?> = toastText

    init {
        contentResolver = app.contentResolver
        initDoujinList()
    }

    private fun initDoujinList() {
        val fullList = (app as MyApplication).fullDoujinList

        if (fullList != null) {
//            tempDoujins = fullList
            doujinList.value = fullList
        } else {
            tempDoujins.clear()
            fetchDoujinsFromDir()
        }
    }

    private suspend fun setResult(doujin: Doujin) {
        withContext(Dispatchers.Main) {
            tempDoujins.add(doujin)
            doujinList.value = tempDoujins

            fetchService?.enqueue(doujin.path)
        }
    }

    private fun fetchDoujinsFromDir() {
        viewModelScope.launch {
            isSyncing.value = true

            withContext(Dispatchers.IO) {
                val includedPaths = repo.pathDao.getAllBlocking()
                for (folder in includedPaths) {
                    walk(folder.dir, folder.dir)
                }
            }

            (app as MyApplication).fullDoujinList = tempDoujins
            isSyncing.value = false

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

    fun fetchMetadataAll() {
        val context = app.applicationContext
        FetchMetadataService.startService(context)

        val connection = object: ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                fetchService = (service as FetchMetadataService.FetchBinder).getService()

                val dirs = tempDoujins.map { doujin -> doujin.path }

                fetchService?.enqueue(dirs)
                fetchService?.startFetching()
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                fetchService = null
            }
        }

        val bindIntent = Intent(context, FetchMetadataService::class.java)
        context.bindService(bindIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onCleared() {
        super.onCleared()
        fetchService = null
    }

}
