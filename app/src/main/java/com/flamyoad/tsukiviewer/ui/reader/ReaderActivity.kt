package com.flamyoad.tsukiviewer.ui.reader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.BottomThumbnailAdapter
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
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
class ReaderActivity : AppCompatActivity(), BottomThumbnailAdapter.OnItemClickListener {
    private lateinit var viewModel: ReaderViewModel

    private var positionFromImageGrid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        viewModel = ViewModelProvider(this).get(ReaderViewModel::class.java)

        positionFromImageGrid = intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        initToolbar()
        initReaderScreen()
        initBottomThumbnails()
        initPageIndicator()
        hideStatusBar()
    }

    override fun onBackPressed() {
        val positionInImageGrid = intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        val positionInViewPager = viewpager.currentItem

        if (positionInImageGrid == positionInViewPager) {
            setResult(Activity.RESULT_CANCELED)

        } else {
            val intent = Intent()
            intent.putExtra(DoujinImagesAdapter.POSITION_AFTER_EXITING_READER, positionInViewPager)
            setResult(Activity.RESULT_OK, intent)
        }

        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_show_exif -> {}
        }
        return true
    }

    private fun initToolbar() {
        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH)

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

    private fun initReaderScreen() {
        viewpager.offscreenPageLimit = 3

        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH)

        viewModel.scanForImages(currentDir)

        val imageAdapter = ImageFragmentStateAdapter(supportFragmentManager)
        viewpager.adapter = imageAdapter

        viewModel.imageList().observe(this, Observer {
            imageAdapter.setList(it)
            if (viewModel.currentPath.isBlank()) {
                viewpager.setCurrentItem(positionFromImageGrid, false)
            }

            viewModel.currentPath = currentDir
        })

        viewModel.totalImageCount().observe(this, Observer {
            setPageIndicatorNumber(1)
        })
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

    private fun hideStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
    }

    private fun initPageIndicator() {
        viewpager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == SCROLL_STATE_DRAGGING) {
                    toggleBottomSheet(View.INVISIBLE)
                }
            }

            override fun onPageSelected(position: Int) {
                setPageIndicatorNumber(position + 1)

                val thumbnailLayoutManager = bottomListThumbnails.layoutManager as LinearLayoutManager
                thumbnailLayoutManager.scrollToPosition(position)
            }
        })

        bottomSheetOpener.setOnClickListener {
            toggleBottomSheet(View.VISIBLE)
        }
    }

    private fun setPageIndicatorNumber(number: Int) {
        val pageNumber = "Page: ${number} / ${viewModel.totalImageCount().value}"
        txtCurrentPageNumber.text = pageNumber
    }

    private fun toggleBottomSheet(visibility: Int) {
        val transition = Slide(Gravity.TOP)
//        transition.duration = 250
        transition.addTarget(R.id.bottomListThumbnails)

        TransitionManager.beginDelayedTransition(parentLayout, transition)

        bottomSheetDialog.visibility = visibility

        if (visibility == View.VISIBLE) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }
    }

    override fun onThumbnailClick(adapterPosition: Int) {
        viewpager.setCurrentItem(adapterPosition, false)

        val thumbnailLayoutManager = bottomListThumbnails.layoutManager as LinearLayoutManager
        thumbnailLayoutManager.scrollToPosition(adapterPosition)
    }
}
