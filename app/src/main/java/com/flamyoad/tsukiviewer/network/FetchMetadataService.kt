package com.flamyoad.tsukiviewer.network

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.flamyoad.tsukiviewer.MainActivity
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.repository.MetadataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class FetchMetadataService : Service() {
    private val CHANNEL_ID = "FetchMetadataService"

    private val NOTIFICATION_ID = 1234

    private val ACTION_CLOSE = "action_close"

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private var job: Job = Job()

    companion object {
        private val DOUJIN_PATH = "doujin_path"
        private lateinit var metadataRepo: MetadataRepository

        fun startService(context: Context, dirPath: String) {
            val startIntent = Intent(context, FetchMetadataService::class.java)
            startIntent.putExtra(DOUJIN_PATH, dirPath)

            metadataRepo = MetadataRepository(context)

            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, FetchMetadataService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_CLOSE -> {
                    stopForeground(true)
                    stopSelf()
                }
            }
        }

        val doujinPath = intent?.getStringExtra(DOUJIN_PATH)

        val doujinDir = File(doujinPath)

        fetchMetadata(doujinDir)

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val stopIntent = Intent(this, FetchMetadataService::class.java)
        stopIntent.action = ACTION_CLOSE

        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fetching metadata")
            .setContentText(doujinDir.name)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_close_gray_24dp, "Stop", stopPendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
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

    private fun fetchMetadata(dir: File) {
        job = ioScope.launch {
            metadataRepo.fetchMetadata(dir)
        }

        job.invokeOnCompletion {
            stopForeground(true)
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("fetch", "onDestroy is called")
        job.cancel()
    }

}