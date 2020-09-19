package com.flamyoad.tsukiviewer.network

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.ui.fetcher.FetcherStatusActivity
import kotlinx.coroutines.*
import java.io.File
import java.util.*

private const val CHANNEL_ID = "FetchMetadataService"
private const val NOTIFICATION_ID = 1234567880
private const val ACTION_CLOSE = "action_close"

class FetchMetadataService : Service() {

//    private val executor = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val supervisorJob = SupervisorJob()

    //    private val coroutineScope = CoroutineScope(executor + supervisorJob)
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)

    private val binder = FetchBinder()

    private val queue: Queue<File> = LinkedList<File>()

    var ongoingQueue: Boolean = false

    var onlyFetchingOneItem: Boolean = false

    private val itemHistory: MutableLiveData<MutableList<File>> = MutableLiveData(mutableListOf())

    fun itemHistory(): LiveData<MutableList<File>> = itemHistory

    private val currentItem: MutableLiveData<File> = MutableLiveData()

    fun currentItem(): LiveData<File> = currentItem

    private var notificationBuilder: NotificationCompat.Builder? = null

    companion object {
        private val DOUJIN_PATH = "doujin_path"
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
                val doujinPath = it.getStringExtra(DOUJIN_PATH) ?: ""
                val doujinDir = File(doujinPath)

                if (doujinPath.isNotBlank()) {
                    coroutineScope.launch {
                        fetchSingleMetadata(doujinDir)
                    }
                }

                createNotificationChannel()
                val notification = createNotification(doujinDir.name)
                startForeground(NOTIFICATION_ID, notification)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun enqueue(file: File, onlyOneItem: Boolean) {
        queue.add(file)

        val temp = itemHistory.value
        temp?.add(file)
        itemHistory.value = temp

        startFetching()
    }

    fun enqueue(files: List<File>) {
        queue.addAll(files)

        val temp = itemHistory.value
        temp?.addAll(files)
        itemHistory.value = temp

        startFetching()
    }

    private fun fetchSingleMetadata(dir: File) {
        val job = coroutineScope.launch {
            metadataRepo?.fetchMetadata(dir)
        }

        job.invokeOnCompletion {
            if (!ongoingQueue && queue.isEmpty()) {
                stopForeground(false)
                stopSelf()
            }
        }
    }

    fun startFetching() {
        while (queue.isNotEmpty()) {
            val dir = queue.poll()
            if (dir != null) {
                currentItem.value = dir
                fetchSingleMetadata(dir)

            }
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

    private fun createNotification(text: String): Notification {
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
                .setContentTitle("Fetching metadata")
//                .setContentText(text)
                .setOnlyAlertOnce(true) // So when data is updated don't make sound and alert in android 8.0+
                .setOngoing(true) // Ongoing notifications cannot be dismissed by the user
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_close_gray_24dp, "Stop", stopPendingIntent)

        } else {
            // Second time. Just modify the content text of the builder
//            notificationBuilder!!.setContentText(text)
        }

        val notification = notificationBuilder!!.build()

        notificationManager?.notify(NOTIFICATION_ID, notification)

        return notification
    }

    private fun clearData() {
        queue.clear()
        itemHistory.value?.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("fetchService", "onDestroy() is called")
        coroutineScope.cancel()
//        executor.close()
    }

    inner class FetchBinder : Binder() {
        fun getService(): FetchMetadataService {
            return this@FetchMetadataService
        }
    }
}