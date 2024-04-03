package com.flamyoad.tsukiviewer.ui.doujinpage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinPagerAdapter
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.databinding.ActivityDoujinDetailsBinding
import com.flamyoad.tsukiviewer.ui.editor.EditorActivity
import com.flamyoad.tsukiviewer.utils.ActivityStackUtils
import com.flamyoad.tsukiviewer.utils.extensions.toast
import com.google.android.material.snackbar.Snackbar

class DoujinDetailsActivity : AppCompatActivity() {

    private val viewModel: DoujinViewModel by viewModels()

    private lateinit var binding: ActivityDoujinDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoujinDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH) ?: ""
        viewModel.scanForImages(dirPath)

        initViewPager(savedInstanceState)
        initToolbar()

        viewModel.snackbarText.observe(this, Observer { text ->
            if (text.isNullOrBlank()) {
                return@Observer
            }

            Snackbar.make(binding.rootView, text, Snackbar.LENGTH_LONG)
                .show()

            viewModel.snackbarText.value = ""
        })

        viewModel.directoryNoLongerExists().observe(this, Observer { notExists ->
            if (notExists) {
                toast("Directory does not exist. Renamed or deleted?")
                finish()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        handleBackPress()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                handleBackPress()
            }

            R.id.action_open_in_browser -> {
                openBrowser()
            }

            R.id.action_clear_metadata -> {
                showClearDataDialog()
            }

            R.id.action_edit -> {
                val dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)
                val doujinTitle = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_NAME)

                val newIntent = Intent(this, EditorActivity::class.java)
                newIntent.putExtra(EditorActivity.DOUJIN_FILE_PATH, dirPath)
                newIntent.putExtra(EditorActivity.DOUJIN_NAME, doujinTitle)

                startActivity(newIntent)
            }
        }
        return false
    }

    private fun initViewPager(savedInstanceState: Bundle?) {
        val adapterViewPager = DoujinPagerAdapter(supportFragmentManager)
        binding.viewpager.adapter = adapterViewPager
        binding.tabLayout.setupWithViewPager(binding.viewpager)

        viewModel.landingPage().observe(this, Observer {
            // Do not trigger on screen rotation. Only trigger on first time when entering activity
            if (savedInstanceState != null) return@Observer

            when (it) {
                LandingPageMode.DoujinDetails -> binding.viewpager.setCurrentItem(0, false)
                LandingPageMode.ImageGrid -> binding.viewpager.setCurrentItem(1, false)
                else -> binding.viewpager.setCurrentItem(0, false)
            }
        })

        binding.viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                invalidateOptionsMenu()
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
        })
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel.detailWithTags.observe(this, Observer {
            if (it != null) {
                supportActionBar?.title = it.doujinDetails.shortTitleEnglish
            } else {
                supportActionBar?.title = "Doujin Details"
            }
        })
    }

    private fun showClearDataDialog() {
        val dialog = DialogRemoveMetadata.newInstance()
        dialog.show(supportFragmentManager, "clearDataDialog")
    }

    private fun openBrowser() {
        val nukeCode = viewModel.getNukeCode()

        val name = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_NAME)

        val uri: Uri

//        if (nukeCode != null) {
        if (!(nukeCode == null || nukeCode == "-1")) {
            val address = "https://nhentai.net/g/${nukeCode}"
            uri = Uri.parse(address)
        } else {
            // "https://nhentai.net/search/?q=${urlEncodedName}"
            uri = Uri.parse("https://nhentai.net/search")
                .buildUpon()
                .appendQueryParameter("q", name)
                .build()
        }

        val browserIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = uri
        }

        if (browserIntent.resolveActivity(packageManager) != null) {
            startActivity(browserIntent)
        } else {
            toast("Cannot open browser")
        }
    }

    private fun handleBackPress() {
        finish()
        ActivityStackUtils.popAndResumePrevActivity(this)
    }

    companion object {
        private const val VIEWPAGER_POSITION = "viewpager_position"
    }
}
