package com.flamyoad.tsukiviewer.ui.reader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.ui.reader.recents.RecentTabsActivity
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabFragmentAdapter
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabListener
import com.flamyoad.tsukiviewer.utils.extensions.reduceDragSensitivity
import com.flamyoad.tsukiviewer.utils.extensions.toast
import kotlinx.android.synthetic.main.activity_reader.*
import java.io.File

const val KEY_DOWN_INTENT = "key_down_intent"
const val KEY_CODE = "key_code"

class ReaderActivity : AppCompatActivity(), ViewPagerListener, ReaderTabListener, LastReadPageNumberListener {

    private val viewModel: ReaderViewModel by viewModels()

    private var positionFromImageGrid = 0

    private var tabFragmentAdapter: ReaderTabFragmentAdapter? = null

    private var savedStateViewPagerIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        viewModel.directoryNoLongerExists().observe(this, Observer { notExists ->
            if (notExists) {
                toast("Directory not found. Renamed or deleted?")
                finish()
            }
        })

        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""

        positionFromImageGrid = intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        viewModel.insertRecentTab(currentDir)

        initViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_reader, menu)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECENT_TAB_REQUEST_CODE && resultCode == RESULT_OK) {
            val tabId = data?.getLongExtra(RecentTabsActivity.CHOSEN_TAB_ID, -1L) ?: -1L
            if (tabId == -1L) {
                return
            }
            viewModel.setCurrentTab(tabId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(VIEWPAGER_INDEX ,viewPager.currentItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedStateViewPagerIndex = savedInstanceState.getInt(VIEWPAGER_INDEX)
    }

    private fun initViewPager() {
        viewPager.reduceDragSensitivity()

        tabFragmentAdapter = ReaderTabFragmentAdapter(this)

        tabFragmentAdapter?.setFirstItem(
            intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: "",
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)
        )

        viewPager.adapter = tabFragmentAdapter

        viewModel.recentTabs.observe(this, Observer {
            tabFragmentAdapter?.setList(it)

            val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""
            val position = tabFragmentAdapter?.getTabPosition(File(currentDir))
            if (position != null) {
                viewPager.setCurrentItem(position, false)
            }
        })

        viewModel.currentTab().observe(this, Observer {
            if (savedStateViewPagerIndex != -1) {
                viewPager.setCurrentItem(savedStateViewPagerIndex, false)
                return@Observer
            }

            val position = tabFragmentAdapter?.getTabPosition(it.id ?: -1)
            if (position as Int != -1) {
                viewPager.setCurrentItem(position, false)
            }
        })
    }

    override fun onBackPressed() {
        returnLastReadPageNumber()
        super.onBackPressed()
    }

    override fun quitActivity() {
        returnLastReadPageNumber()
        finish()
    }

    override fun setUserInputEnabled(isEnabled: Boolean) {
        viewPager.isUserInputEnabled = isEnabled
    }

    override fun savePageNumber(pageNumber: Int) {
        viewModel.lastReadImagePosition = pageNumber
    }

    private fun returnLastReadPageNumber() {
        val positionInImageGrid =
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        val positionInReader = viewModel.lastReadImagePosition

        // Sends back the position of image currently being viewed if it's different with
        // the position of opened image in previous screen.
        if (positionInImageGrid != positionInReader) {
            val intent = Intent()
            intent.putExtra(DoujinImagesAdapter.POSITION_AFTER_EXITING_READER, positionInReader)
            setResult(Activity.RESULT_OK, intent)

        } else {
            setResult(Activity.RESULT_CANCELED)
        }
    }

    companion object {
        private const val VIEWPAGER_INDEX = "viewpager_index"
        const val RECENT_TAB_REQUEST_CODE = 101
    }
}
