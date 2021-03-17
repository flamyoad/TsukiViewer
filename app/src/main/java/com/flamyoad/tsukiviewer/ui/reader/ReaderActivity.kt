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
import com.flamyoad.tsukiviewer.utils.extensions.toast
import kotlinx.android.synthetic.main.activity_reader.*
import java.io.File

const val KEY_DOWN_INTENT = "key_down_intent"
const val KEY_CODE = "key_code"

class ReaderActivity : AppCompatActivity(), ViewPagerListener {

    private val viewModel: ReaderViewModel by viewModels()

    private var positionFromImageGrid = 0

    private var tabFragmentAdapter: ReaderTabFragmentAdapter? = null

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

    private fun initViewPager() {
        tabFragmentAdapter = ReaderTabFragmentAdapter(this)

        tabFragmentAdapter?.setFirstItem(
            intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: "",
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)
        )

        viewPager.adapter = tabFragmentAdapter
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = 1

        viewModel.recentTabs.observe(this, Observer {
            tabFragmentAdapter?.setList(it)

            val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""
            val position = tabFragmentAdapter?.getTabPosition(File(currentDir))
            if (position != null) {
                viewPager.setCurrentItem(position, false)
            }
        })

        viewModel.currentTab().observe(this, Observer {
            val position = tabFragmentAdapter?.getTabPosition(it.id ?: -1)
            if (position != null) {
                viewPager.setCurrentItem(position, false)
            }
        })
    }

    override fun setUserInputEnabled(enabled: Boolean) {
        viewPager.isUserInputEnabled = enabled
    }

    override fun onBackPressed() {
        // Hides bottom sheet and toolbar if they are present. Otherwise go back to previous activity.
//        if (bottomSheetDialog.visibility == View.VISIBLE) {
//            toggleBottomSheet(View.INVISIBLE)
//            return
//        }
//
//        handleActivityTermination()
        super.onBackPressed()
        // onBackPressed() quits current activity so it must be called last.
        // Otherwise, lines below onBackPressed() won't be called
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        // Only intercept volume button events
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            val intent = Intent(KEY_DOWN_INTENT).apply {
//                putExtra(KEY_CODE, keyCode)
//            }
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//            return true
//        } else {
//            return super.onKeyDown(keyCode, event)
//        }
//    }

    private fun handleActivityTermination() {
        val positionInImageGrid =
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        val positionInReader = viewModel.currentImagePosition

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
        const val RECENT_TAB_REQUEST_CODE = 101
    }

}
