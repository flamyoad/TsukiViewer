package com.flamyoad.tsukiviewer.ui.fetcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.FetchHistoryAdapter
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import kotlinx.android.synthetic.main.activity_fetcher_status.*

class FetcherStatusActivity : AppCompatActivity() {

    private var fetchService: FetchMetadataService? = null

    private val adapter = FetchHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetcher_status)

        doBindService()
    }

    private fun doBindService() {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                fetchService = (service as FetchMetadataService.FetchBinder).getService()
                observeChanges()
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                fetchService = null
            }
        }

        val bindIntent = Intent(this@FetcherStatusActivity, FetchMetadataService::class.java)
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE)

        initList()
    }

    private fun initList() {
        adapter.setHasStableIds(true)

        listItems.adapter = adapter
        listItems.layoutManager = LinearLayoutManager(this)
        listItems.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    private fun observeChanges() {
        val service = fetchService

        if (service != null) {
            service.fetchPercentage.observe(this, Observer {
                if (it != null) {
                    txtProgress.text = it.getProgress()
                    txtPercentage.text = it.getPercentString()
                    progressBar.progress = it.getPercent()
                }
            })

            service.fetchHistories().observe(this, Observer {
                adapter.setList(it)
                txtProcessed.text = getString(R.string.processed_item_text, it.size)
            })

            service.currentItem().observe(this, Observer {
                txtCurrentItem.text = it.name
            })

            service.dirList().observe(this, Observer {})
        }
    }

}
