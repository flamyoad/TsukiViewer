package com.flamyoad.tsukiviewer.ui.doujinpage

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinPagerAdapter
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
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
