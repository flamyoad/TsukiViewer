package com.flamyoad.tsukiviewer.network

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.ui.fetcher.FetcherStatusActivity
import kotlinx.coroutines.*
import java.io.File
import kotlin.random.Random

private const val CHANNEL_ID = "FetchMetadataService"
private const val NOTIFICATION_ID = 1010
private const val ACTION_CLOSE = "action_close"
private const val DELAY_BETWEEN_REQUEST: Long = 50 // ms

class FetchMetadataService : Service() {

    private val supervisorJob = SupervisorJob()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)

    private val binder = FetchBinder()

    private val dirList: MutableLiveData<List<File>> = MutableLiveData()
    fun dirList(): LiveData<List<File>> = dirList

    private val currentItem: MutableLiveData<File> = MutableLiveData()
    fun currentItem(): LiveData<File> = currentItem

    private val fetchHistories = MutableLiveData<MutableList<FetchHistory>>()
    fun fetchHistories(): LiveData<MutableList<FetchHistory>> = fetchHistories

    val fetchPercentage = MediatorLiveData<FetchPercentage>()

    private var notificationBuilder: NotificationCompat.Builder? = null

    init {
        fetchPercentage.addSource(fetchHistories) { fetched ->
            val prev = fetchPercentage.value
            fetchPercentage.value = prev?.copy(fetched = fetched.size)
        }

        fetchPercentage.addSource(dirList) { dir ->
            val prev = fetchPercentage.value
            fetchPercentage.value = prev?.copy(total = dir.size)
        }
    }

    companion object {
        private const val DOUJIN_PATH = "doujin_path"
        private var metadataRepo: MetadataRepository? = null
        private var notificationManager: NotificationManager? = null

        fun initComponents(context: Context) {
            if (metadataRepo == null) {
                metadataRepo = MetadataRepository(context)
            }

            if (notificationManager == null) {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
        }

        fun startService(context: Context) {
            initComponents(context)

            val startIntent = Intent(context, FetchMetadataService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun startService(context: Context, dirPath: String) {
            val startIntent = Intent(context, FetchMetadataService::class.java)
            startIntent.putExtra(DOUJIN_PATH, dirPath)

            initComponents(context)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, FetchMetadataService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == ACTION_CLOSE) {
                Log.d("fetchService", "ACTION_CLOSE intent is received")
                stopForeground(true)
                stopSelf()

            } else {
                createNotificationChannel()

                val doujinPath = it.getStringExtra(DOUJIN_PATH) ?: ""
                if (doujinPath.isNotBlank()) {
                    val doujinDir = File(doujinPath)
                    fetchSingle(doujinDir)
                }

                val notification = createNotification("")
                startForeground(NOTIFICATION_ID, notification)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun enqueueList(dirList: List<File>) {
        this.dirList.value = dirList

        val batchJob = coroutineScope.launch {
            for ((index, dir) in dirList.withIndex()) {
                withContext(Dispatchers.Main) {
                    currentItem.value = dir
                    createNotification(dir.name, index + 1, dirList.size)
                }

//                val result: Pair<FetchStatus, String> = metadataRepo!!.fetchMetadata(dir)
                val result = createMockResult(dir)

                val history = FetchHistory(
                    dir = dir,
                    status = result.first,
                    doujinName = result.second
                )

                val fetchStatus = result.first
                when (fetchStatus) {
                    FetchStatus.SUCCESS -> postFetchHistory(history)
                    FetchStatus.NO_MATCH -> postFetchHistory(history)
                }

                if (fetchStatus != FetchStatus.ALREADY_EXISTS) {
                    delay(DELAY_BETWEEN_REQUEST) // 50ms between each network request
                }
            }
        }

        batchJob.invokeOnCompletion {
            createNotification("All directories have been processed")
            stopForeground(false)
            stopSelf()
        }
    }

    fun createMockResult(dir: File): Pair<FetchStatus, String> {
        val status = when (Random.nextBoolean()) {
            true -> FetchStatus.SUCCESS
            false -> FetchStatus.NO_MATCH
        }
        return Pair(status, dir.name)
    }

    private fun fetchSingle(dir: File) {
        val job = coroutineScope.launch {
            val result = metadataRepo!!.fetchMetadata(dir)

            val fetchStatus = result.first
            when (fetchStatus) {
                FetchStatus.NO_MATCH -> showToast("No matching result for this directory")
            }
        }

        job.invokeOnCompletion {
            if (!coroutineScope.isActive) {
                stopForeground(true)
                stopSelf()
            }
        }
    }

    private suspend fun postFetchHistory(history: FetchHistory) {
        withContext(Dispatchers.Main) {
            val list = fetchHistories.value ?: mutableListOf()
            list.add(history)

            fetchHistories.value = list
        }
    }

    // You must create notification channel in Android O and above
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Tsuki Viewer",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(text: String, progress: Int = 0, max: Int = 100): Notification {
        // First time building the notification. Initialize all necessary things
        if (notificationBuilder == null) {
            notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            val startActivityIntent = Intent(this, FetcherStatusActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, 0)

            val stopIntent = Intent(this, FetchMetadataService::class.java)
            stopIntent.action = ACTION_CLOSE

            val stopPendingIntent =
                PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            notificationBuilder!!
                .setContentTitle("Scanning for directories")
                .setOnlyAlertOnce(true) // So when data is updated don't make sound and alert in android 8.0+
                .setOngoing(true) // Ongoing notifications cannot be dismissed by the user
                .setSmallIcon(R.drawable.ic_sd_card_gray_24dp)
                .setContentIntent(pendingIntent)
                .setProgress(max, progress, true)
                .addAction(R.drawable.ic_close_gray_24dp, "Stop", stopPendingIntent)

        } else {
            // Second time. Just modify the content of the builder
            notificationBuilder!!.apply {
                setContentTitle("Fetching metadata")
                setContentText(text)
                setProgress(max, progress, false)
            }
        }

        val notification = notificationBuilder!!.build()

        notificationManager?.notify(NOTIFICATION_ID, notification)

        return notification
    }

    private fun showToast(message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    inner class FetchBinder : Binder() {
        fun getService(): FetchMetadataService {
            return this@FetchMetadataService
        }
    }
}