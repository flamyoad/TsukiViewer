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
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import com.flamyoad.tsukiviewer.ui.fetcher.FetcherStatusActivity
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class FetchMetadataService : Service() {

    private val CHANNEL_ID = "FetchMetadataService"

    private val NOTIFICATION_ID = 1234567880

    private val ACTION_CLOSE = "action_close"

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var job: Job = Job()

    private val binder = FetchBinder()

    private val queue: Queue<File> = LinkedList<File>()

    var ongoingQueue: Boolean = false

    companion object {
        private val DOUJIN_PATH = "doujin_path"
        private var metadataRepo: MetadataRepository? = null
        private var notificationManager: NotificationManager? = null

        fun startService(context: Context) {
            if (metadataRepo == null) {
                metadataRepo = MetadataRepository(context)
            }

            if (notificationManager == null) {
                notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            val startIntent = Intent(context, FetchMetadataService::class.java)

            ContextCompat.startForegroundService(context, startIntent)
        }

        fun startService(context: Context, dirPath: String) {
            val startIntent = Intent(context, FetchMetadataService::class.java)
            startIntent.putExtra(DOUJIN_PATH, dirPath)

            if (metadataRepo == null) {
                metadataRepo = MetadataRepository(context)
            }

            if (notificationManager == null) {
                notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

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
                stopForeground(false)
                stopSelf()
            }
        }

        val doujinPath = intent?.getStringExtra(DOUJIN_PATH) ?: ""

        val doujinDir = File(doujinPath)

        if (doujinPath.isNotBlank()) {
            fetchSingleMetadata(doujinDir)
        }

        createNotificationChannel()

        val notification = createNotification(doujinDir.name)

        startForeground(NOTIFICATION_ID, notification)

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun startFetching() {
        fetchMultipleMetadata()
    }

    fun enqueue(file: File) {
        queue.add(file)
        fetchMultipleMetadata()
    }

    fun enqueue(files: List<File>) {
        queue.addAll(files)
        fetchMultipleMetadata()
    }

    private fun fetchSingleMetadata(dir: File) {
        createNotification(dir.name)

        job = coroutineScope.launch {
            metadataRepo?.fetchMetadata(dir)
            // delay() is not same as Thread.sleep() wtf
        }

        job.invokeOnCompletion {
            if (!ongoingQueue && queue.isEmpty()) {
                stopForeground(true)
                stopSelf()
            }
        }
    }

    private fun fetchMultipleMetadata() {
        while (queue.isNotEmpty()) {
            val dir = queue.poll()
            if (dir != null) {
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
        val startActivityIntent = Intent(this, FetcherStatusActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, 0)

        val stopIntent = Intent(this, FetchMetadataService::class.java)
        stopIntent.action = ACTION_CLOSE

        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fetching metadata")
            .setContentText(text)
            .setOnlyAlertOnce(true) // So when data is updated don't make sound and alert in android 8.0+
//            .setOngoing(true) // Ongoing notifications cannot be dismissed by the user
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_close_gray_24dp, "Stop", stopPendingIntent)
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)

        return notification
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    inner class FetchBinder: Binder() {
        fun getService(): FetchMetadataService {
            return this@FetchMetadataService
        }
    }
}