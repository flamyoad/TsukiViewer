package com.flamyoad.tsukiviewer.ui.doujinpage

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinPagerAdapter
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import com.flamyoad.tsukiviewer.ui.editor.EditorActivity
import kotlinx.android.synthetic.main.activity_doujin_details.*

class DoujinDetailsActivity : AppCompatActivity() {

    private lateinit var viewmodel: DoujinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doujin_details)

        viewmodel = ViewModelProvider(this).get(DoujinViewModel::class.java)

        handleIntent()
        initTabLayout()
        initToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_doujin_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.action_sync -> {
                val dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)
                FetchMetadataService.startService(this, dirPath)
            }

            R.id.action_edit -> {
                val dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)
                val doujinTitle = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_NAME)

                val newIntent = Intent(this, EditorActivity::class.java)
                newIntent.putExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH, dirPath)
                newIntent.putExtra(LocalDoujinsAdapter.DOUJIN_NAME, doujinTitle)

                startActivity(newIntent)
            }
        }
        return true
    }

    private fun handleIntent() {
        val dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)
        viewmodel.scanForImages(dirPath)
    }

    private fun initTabLayout() {
        val adapterViewPager = DoujinPagerAdapter(supportFragmentManager)
        viewpager.adapter = adapterViewPager
        tabLayout.setupWithViewPager(viewpager)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewmodel.detailWithTags.observe(this, Observer {
            if (it != null) {
                supportActionBar?.title = it.doujinDetails.shortTitleEnglish
            } else {
                supportActionBar?.title = "Doujin Details"
            }
        })
    }
}
