package com.flamyoad.tsukiviewer.ui.doujinpage

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinPagerAdapter
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.Source
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import com.flamyoad.tsukiviewer.ui.editor.EditorActivity
import com.flamyoad.tsukiviewer.ui.home.local.SelectSourceListener
import com.flamyoad.tsukiviewer.utils.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_doujin_details.*
import java.util.*

class DoujinDetailsActivity : AppCompatActivity() {

    private val viewModel: DoujinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doujin_details)

        handleIntent()
        initViewPager()
        initToolbar()

        viewModel.snackbarText.observe(this, Observer { text ->
            if (text.isNullOrBlank()) {
                return@Observer
            }

            Snackbar.make(rootView, text, Snackbar.LENGTH_LONG)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
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
                newIntent.putExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH, dirPath)
                newIntent.putExtra(LocalDoujinsAdapter.DOUJIN_NAME, doujinTitle)

                startActivity(newIntent)
            }
        }
        return false
    }

    private fun handleIntent() {
        val dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH) ?: ""
        viewModel.scanForImages(dirPath)
    }

    private fun initViewPager() {
        val adapterViewPager = DoujinPagerAdapter(supportFragmentManager)
        viewpager.adapter = adapterViewPager
        tabLayout.setupWithViewPager(viewpager)

        viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                invalidateOptionsMenu()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {}
        })
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
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


    private fun showConfirmResyncDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Reset to original tags")
            .setMessage("Previous tags that have been added manually will be erased. Continue?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                // Update tags in DB
                viewModel.resetTags()
                dialogInterface.dismiss()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            .create()

        dialog.show()
    }

    private fun showClearDataDialog() {
        val dialog = DialogRemoveMetadata.newInstance()
        dialog.show(supportFragmentManager, "clearDataDialog")
    }

    private fun openBrowser() {
        val nukeCode = viewModel.getNukeCode()

        val name = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_NAME)

        val uri: Uri

        if (nukeCode != null) {
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
}
