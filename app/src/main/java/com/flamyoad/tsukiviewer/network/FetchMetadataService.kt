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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Source
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.ui.fetcher.FetcherStatusActivity
import kotlinx.coroutines.*
import java.io.File
import java.util.*

private const val CHANNEL_ID = "FetchMetadataService"
private const val NOTIFICATION_ID = 182051
private const val ACTION_CLOSE = "action_close"
private const val DELAY_BETWEEN_REQUEST: Long = 0 // ms

class FetchMetadataService : Service() {
    private val supervisorJob = SupervisorJob()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)

    private val binder = FetchBinder()

    private val currentItem: MutableLiveData<File> = MutableLiveData()
    fun currentItem(): LiveData<File> = currentItem

    private val fetchHistories = MutableLiveData<MutableList<FetchHistory>>()
    fun fetchHistories(): LiveData<MutableList<FetchHistory>> = fetchHistories

    val fetchPercentage: LiveData<FetchPercentage>

    private var notification: Notification? = null

    private var notificationBuilder: NotificationCompat.Builder? = null

    private var notificationManager: NotificationManager? = null

    private var batchJob: Job? = null

    private var singleJob: Job? = null

    private var dirItemCount: Int = 0

    // Data class's copy() method returns null if the object to copy from is null
    init {
        fetchPercentage = fetchHistories.map {
            return@map FetchPercentage(it.size, dirItemCount)
        }
    }

    companion object {
        private const val DOUJIN_PATH = "doujin_path"
        private const val SOURCE_FLAGS = "source_flags"
        private var metadataRepo: MetadataRepository? = null

        fun initComponents(context: Context) {
            if (metadataRepo == null) {
                val app = context.applicationContext as MyApplication
                metadataRepo = app.appComponent.metadataRepository()
            }
        }

        fun startService(context: Context) {
            initComponents(context)

            val startIntent = Intent(context, FetchMetadataService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun startService(context: Context, dirPath: String, sources: EnumSet<Source>) {
            val startIntent = Intent(context, FetchMetadataService::class.java)

            val sourceFlags = sources.map { source -> source.name }.toTypedArray()

            startIntent.putExtra(DOUJIN_PATH, dirPath)
            startIntent.putExtra(SOURCE_FLAGS, sourceFlags)

            initComponents(context)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, FetchMetadataService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        prepareNotificationAndStartForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == ACTION_CLOSE) {
                Log.d("fetchService", "ACTION_CLOSE intent is received")
                stopForeground(true)
                stopSelf()

            } else {
                prepareNotificationAndStartForeground()

                val doujinPath = it.getStringExtra(DOUJIN_PATH) ?: ""
                val sourceFlags = it.getStringArrayExtra(SOURCE_FLAGS) ?: emptyArray()

                if (doujinPath.isNotBlank()) {
                    val doujinDir = File(doujinPath)

                    val sources = sourceFlags.map { flag -> Source.valueOf(flag) }

                    fetchSingle(doujinDir, EnumSet.copyOf(sources))
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun prepareNotificationAndStartForeground() {
        // https://stackoverflow.com/questions/46375444/remoteserviceexception-context-startforegroundservice-did-not-then-call-servic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            createNotification("")
            startForeground(NOTIFICATION_ID, notification)
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

            notificationManager!!.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(text: String, progress: Int = 0, max: Int = 100) {
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

        notification = notificationBuilder!!.build()

        notificationManager!!.notify(NOTIFICATION_ID, notification)
    }

    fun enqueueList(dirList: List<File>, sources: EnumSet<Source>) {
        dirItemCount = dirList.size

        // Avoid starting the same job for second time
        if (batchJob != null) {
            return
        }

        batchJob = coroutineScope.launch {
            for ((index, dir) in dirList.withIndex()) {
                withContext(Dispatchers.Main) {
                    currentItem.value = dir
                    createNotification(dir.name, index + 1, dirList.size)
                }

                val result = metadataRepo!!.fetchMetadata(dir, sources)

                val fetchStatus = result.status
                when (fetchStatus) {
                    FetchStatus.SUCCESS -> postFetchHistory(result)
                    FetchStatus.ALREADY_EXISTS -> postFetchHistory(result)
                    FetchStatus.NO_MATCH -> postFetchHistory(result)
                    FetchStatus.NETWORK_ERROR -> { /* Handle error if needed */ }
                    FetchStatus.NONE -> { /* Do nothing */ }
                }

                if (fetchStatus != FetchStatus.ALREADY_EXISTS) {
                    delay(DELAY_BETWEEN_REQUEST)
                }
            }
        }

        batchJob?.invokeOnCompletion {
            stopForeground(false)
            stopSelf()
        }
    }

    private fun fetchSingle(dir: File, sources: EnumSet<Source>) {
        singleJob = coroutineScope.launch {
            val result = metadataRepo!!.fetchMetadata(dir, sources)

            when (result.status) {
                FetchStatus.NO_MATCH -> showToast("No matching result for this directory")
                FetchStatus.SUCCESS,
                FetchStatus.ALREADY_EXISTS,
                FetchStatus.NETWORK_ERROR,
                FetchStatus.NONE -> { /* Do nothing */ }
            }
        }

        singleJob?.invokeOnCompletion {
            Log.d("fetchService", "singleJob::invokeOnCompletion() for ${dir.absolutePath}")
            if (batchJob == null || batchJob?.isActive == false) {
                Log.d("fetchService", "stopForeground() from singleJob::invokeOnCompletion()")
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

    private fun showToast(message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("fetchService", "onDestroy() is called")
        batchJob?.cancel() // is null
        singleJob?.cancel()
        notificationManager!!.cancelAll()
    }

    inner class FetchBinder : Binder() {
        fun getService(): FetchMetadataService {
            return this@FetchMetadataService
        }
    }
}