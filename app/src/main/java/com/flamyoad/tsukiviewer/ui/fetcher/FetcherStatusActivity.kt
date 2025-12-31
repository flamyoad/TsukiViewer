package com.flamyoad.tsukiviewer.ui.fetcher

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.FetchHistoryAdapter
import com.flamyoad.tsukiviewer.databinding.ActivityFetcherStatusBinding
import com.flamyoad.tsukiviewer.service.FetchMetadataService

class FetcherStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFetcherStatusBinding

    private var fetchService: FetchMetadataService? = null

    private var connection: ServiceConnection? = null

    private val adapter = FetchHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetcherStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doBindService()
    }

    override fun onPause() {
        super.onPause()
        connection?.let { unbindService(it) }
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
//        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE)
        bindService(bindIntent, connection, 0)

        initList()
    }

    private fun initList() {
        adapter.setHasStableIds(true)

        binding.listItems.adapter = adapter
        binding.listItems.layoutManager = LinearLayoutManager(this)
        binding.listItems.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    private fun observeChanges() {
        val service = fetchService

        if (service != null) {
            service.fetchPercentage.observe(this, Observer {
                if (it != null) {
                    binding.txtProgress.text = it.getProgress()
                    binding.txtPercentage.text = it.getPercentString()
                    binding.progressBar.progress = it.getPercent()

                    binding.loadingIndicator.visibility = View.GONE
                }
            })

            service.fetchHistories().observe(this, Observer {
                /*
                   https://stackoverflow.com/questions/49726385/listadapter-not-updating-item-in-recyclerview

                   In the Service class, the same reference of ArrayList reused and passed into the MutableLiveData.
                   toList() is needed because submitList() assumes each new list is different.
                   If the new list has same reference as the previous list, the call is silently ignored by submitList()
                */
                adapter.submitList(it.toList())
                binding.txtProcessed.text = getString(R.string.processed_item_text, it.size)
            })

            service.currentItem().observe(this, Observer {
                binding.txtCurrentItem.text = it.absolutePath
            })
        }
    }

}
