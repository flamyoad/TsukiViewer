package com.flamyoad.tsukiviewer.ui.fetcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.network.FetchMetadataService

class FetcherStatusActivity : AppCompatActivity() {

    private var fetchService: FetchMetadataService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetcher_status)

        doBindService()
    }

    private fun doBindService() {
        val connection = object: ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                fetchService = (service as FetchMetadataService.FetchBinder).getService()
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                fetchService = null
            }
        }

        val bindIntent = Intent(this@FetcherStatusActivity, FetchMetadataService::class.java)
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE)
    }
}
