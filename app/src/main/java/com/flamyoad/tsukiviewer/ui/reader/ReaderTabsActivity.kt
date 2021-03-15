package com.flamyoad.tsukiviewer.ui.reader

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.ReaderTabAdapter
import kotlinx.android.synthetic.main.activity_reader_tabs.*

class ReaderTabsActivity : AppCompatActivity() {

    private val viewModel: ReaderTabsViewModel by viewModels()

    private val tabAdapter = ReaderTabAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_tabs)
        initReaderHistory()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

//        val drawable = ContextCompat.getDrawable(this, R.drawable.ayumu)
//        Glide.with(this)
//            .load(drawable)
//            .into(imgBackground)
    }

    private fun initReaderHistory() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        listTabs.apply {
            adapter = tabAdapter
            layoutManager = linearLayoutManager
        }

        viewModel.tabList.observe(this, Observer {
            tabAdapter.submitList(it)
        })
    }
}
