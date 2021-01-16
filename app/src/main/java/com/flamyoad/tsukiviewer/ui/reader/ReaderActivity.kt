package com.flamyoad.tsukiviewer.ui.reader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.BottomThumbnailAdapter
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.utils.toast
import kotlinx.android.synthetic.main.activity_reader.*
import java.io.File

/*
    Date of writing: 18/9/2020

    Switched the image slider to androidx.ViewPager.

    ViewPager2 has noticable delay when swiping through the items.
    For example, after swiping from 1st to 2nd item, there is a certain delay before you are able to swipe from 2nd to 3rd item
    The same thing can be observed on RecyclerView with PagerSnapHelper attached to it.

    (NOTE: The slow scrolling is not caused by performance penalty of loading large items)
    I have tested the scrolling speed of RecyclerView with and without PagerSnapHelper.
    The former scrolls very smoothly whereas the latter has delay when swiping through the items.

    Well, they share the same behaviour because ViewPager2 uses PagerSnapHelper internally
    with RecyclerView to simulate the old ViewPager behaviour.

    I have no idea why. Perhaps I should post this on StackOverflow someday.
 */

private const val SWIPE_READER = "swipe_reader"
private const val VERTICAL_READER = "vertical_reader"
const val KEY_DOWN_INTENT = "key_down_intent"
const val KEY_CODE = "key_code"

class ReaderActivity : AppCompatActivity(),
    ReaderListener,
    BottomThumbnailAdapter.OnItemClickListener {

    private val viewModel: ReaderViewModel by viewModels()

    private var positionFromImageGrid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        viewModel.directoryNoLongerExists().observe(this, Observer { notExists ->
            if (notExists) {
                toast("Directory not found. Renamed or deleted?")
                finish()
            }
        })

        positionFromImageGrid =
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        viewModel.readerMode().observe(this, Observer {
            setupReader(it)
            setupSideMenu(it)
        })

        initToolbar()
        initBottomThumbnails()
        initPageIndicator()
        hideStatusBar()

        btnHorizReader.setOnClickListener {
            viewModel.setReaderMode(ReaderMode.HorizontalSwipe)
        }

        btnVertReader.setOnClickListener {
            viewModel.setReaderMode(ReaderMode.VerticalStrip)
        }
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

    private fun setupReader(mode: ReaderMode) {
        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""
        val positionInGrid =
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        viewModel.scanForImages(currentDir)

        val fragment = when (mode) {
            ReaderMode.HorizontalSwipe -> HorizontalSwipeReaderFragment.newInstance(
                currentDir,
                positionInGrid
            )
            ReaderMode.VerticalStrip -> VerticalStripReaderFragment.newInstance(
                currentDir,
                positionInGrid
            )
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, SWIPE_READER)
            .commit()
    }

    private fun setupSideMenu(mode: ReaderMode) {
        // Changes button color according to active/inactive state
        val activeBtnColor = ContextCompat.getColor(this, R.color.read_mode_button_active)
        val inactiveBtnColor = ContextCompat.getColor(this, R.color.read_mode_button_inactive)

        when (mode) {
            ReaderMode.HorizontalSwipe -> {
                btnHorizReader.background.setTint(activeBtnColor)
                btnVertReader.background.setTint(inactiveBtnColor)
            }
            ReaderMode.VerticalStrip -> {
                btnVertReader.background.setTint(activeBtnColor)
                btnHorizReader.background.setTint(inactiveBtnColor)
            }
        }
    }

    private fun initToolbar() {
        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""

        val file = File(currentDir)

        val doujinName = file.name

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = doujinName
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun initBottomThumbnails() {
        val adapter = BottomThumbnailAdapter(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = LinearSnapHelper()

        bottomListThumbnails.adapter = adapter
        bottomListThumbnails.layoutManager = linearLayoutManager
        snapHelper.attachToRecyclerView(bottomListThumbnails)

        viewModel.imageList().observe(this, Observer {
            adapter.setList(it)
            linearLayoutManager.scrollToPosition(positionFromImageGrid)
        })
    }

    private fun initPageIndicator() {
        bottomSheetOpener.setOnClickListener {
            toggleBottomSheet(View.VISIBLE)
        }
    }

    private fun hideStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
    }

    private fun setPageIndicatorNumber(pageNum: Int) {
        val pageNumber = "Page: ${pageNum} / ${viewModel.getTotalImagesCount()}"
        txtCurrentPageNumber.text = pageNumber
    }

    override fun onThumbnailClick(adapterPosition: Int) {
        viewModel.onThumbnailClick(adapterPosition)

        val thumbnailLayoutManager = bottomListThumbnails.layoutManager as LinearLayoutManager
        thumbnailLayoutManager.scrollToPosition(adapterPosition)

        hideReaderModeDialog()
    }

    // Triggers when user selects image in bottom dialog. We do not want the right side dialog to continue blocking the screen
    private fun hideReaderModeDialog() {
        val rightSlide = Slide(Gravity.END).apply {
            addTarget(R.id.readerModeDialog)
        }
        TransitionManager.beginDelayedTransition(readerModeDialog, rightSlide)
        readerModeDialog.visibility = View.GONE
    }

    override fun onPageChange(pageNum: Int) {
        viewModel.currentImagePosition = pageNum
        setPageIndicatorNumber(pageNum + 1)

        val bottomLayoutManager = bottomListThumbnails.layoutManager as LinearLayoutManager
        bottomLayoutManager.scrollToPosition(pageNum)
    }

    override fun toggleBottomSheet(visibility: Int) {
        val btmSlide = Slide(Gravity.BOTTOM).apply {
            addTarget(R.id.bottomSheetDialog)
        }

        val rightSlide = Slide(Gravity.END).apply {
            addTarget(R.id.readerModeDialog)
        }

        TransitionManager.beginDelayedTransition(bottomSheetDialog, btmSlide)
        TransitionManager.beginDelayedTransition(readerModeDialog, rightSlide)

        bottomSheetDialog.visibility = visibility
        readerModeDialog.visibility = visibility

        when (visibility) {
            View.VISIBLE -> supportActionBar?.show()
            else -> supportActionBar?.hide()
        }
    }

    override fun onBackPressed() {
        // Hides bottom sheet and toolbar if they are present. Otherwise go back to previous activity.
        if (bottomSheetDialog.visibility == View.VISIBLE) {
            toggleBottomSheet(View.INVISIBLE)
            return
        }

        handleActivityTermination()
        super.onBackPressed()
        // onBackPressed() quits current activity so it must be called last.
        // Otherwise, lines below onBackPressed() won't be called
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Only intercept volume button events
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val intent = Intent(KEY_DOWN_INTENT).apply {
                putExtra(KEY_CODE, keyCode)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

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
}
