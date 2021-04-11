package com.flamyoad.tsukiviewer.ui.reader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.model.RecentTab
import com.flamyoad.tsukiviewer.ui.reader.recents.RecentTabsActivity
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabFragmentAdapter
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabListener
import com.flamyoad.tsukiviewer.utils.extensions.reduceDragSensitivitySlightly
import com.flamyoad.tsukiviewer.utils.extensions.toast
import kotlinx.android.synthetic.main.activity_reader.*


const val MY_KEY_DOWN_INTENT = "my_key_down_intent"
const val KEY_CODE = "key_code"

class ReaderActivity : AppCompatActivity(), ViewPagerListener, ReaderTabListener, LastReadPageNumberListener {

    private val viewModel: ReaderViewModel by viewModels()

    private var positionFromImageGrid = 0

    private var viewPagerIndex: Int = -1

    private var chosenTabId: Long? = null

    private var tabChosenFromRecentTabs: Boolean = false

    private lateinit var tabFragmentAdapter: ReaderTabFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        viewModel.directoryNoLongerExists().observe(this, Observer { notExists ->
            if (notExists) {
                toast("Directory not found. Renamed or deleted?")
//                finish()
            }
        })
        positionFromImageGrid = intent.getIntExtra(
            DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER,
            0
        )

        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""
        viewModel.insertRecentTab(currentDir)

        setupViewPager()
        viewPager.reduceDragSensitivitySlightly()
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
            tabChosenFromRecentTabs = true
            chosenTabId = tabId
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(VIEWPAGER_INDEX, viewPager.currentItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewPagerIndex = savedInstanceState.getInt(VIEWPAGER_INDEX)
    }

    /* dispatchKeyEvent is preferred over onKeyDown
       This is because onKeyDown will still emit the volume click sound, whereas dispatchKeyEvent does not

       https://stackoverflow.com/questions/24121644/dispatchkeyevent-invoking-twice
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val keyCode = event?.keyCode
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (event.action != KeyEvent.ACTION_DOWN) return true

            val intent = Intent(MY_KEY_DOWN_INTENT)
            intent.putExtra(KEY_CODE, keyCode)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            return true
        } else {
            return super.dispatchKeyEvent(event)
        }
    }

    private fun setupViewPager() {
        tabFragmentAdapter = ReaderTabFragmentAdapter(this)

        tabFragmentAdapter.setFirstItem(
            intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: "",
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)
        )

        viewPager.apply {
            adapter = tabFragmentAdapter
        }

        viewModel.recentTabs.observe(this, Observer {
            tabFragmentAdapter.setList(it)
            val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""
            val currentTab = when (tabChosenFromRecentTabs) {
                true -> tabFragmentAdapter.getTab(chosenTabId ?: -1)
                false -> tabFragmentAdapter.getTab(currentDir)
            }

            if (currentTab != null) {
                switchReaderTab(currentTab)
            }

            (viewPager.getChildAt(0) as RecyclerView).layoutManager!!.isItemPrefetchEnabled = false
        })

        viewModel.currentTab().observe(this, Observer {
            switchReaderTab(it)
        })
    }

    private fun switchReaderTab(tab: RecentTab) {
        if (viewPagerIndex != -1) {
            viewPager.setCurrentItem(viewPagerIndex, false)
            viewPagerIndex = -1 // resets the index
            return
        }

        val position = tabFragmentAdapter.getTabPosition(tab.id ?: return)
        viewPager.setCurrentItem(position, false)
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
