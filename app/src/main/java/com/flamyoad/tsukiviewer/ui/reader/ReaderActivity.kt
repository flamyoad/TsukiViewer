package com.flamyoad.tsukiviewer.ui.reader

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
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

    private lateinit var viewmodel: ReaderViewModel

    private var imagePositionInList = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        viewmodel = ViewModelProvider(this).get(ReaderViewModel::class.java)

        imagePositionInList = intent.getIntExtra(DoujinImagesAdapter.ADAPTER_POSITION, 0)

        initToolbar()
        initReaderScreen()
        initBottomThumbnails()
        initPageIndicator()
        hideStatusBar()
    }

    private fun initToolbar() {
        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH)

        val file = File(currentDir)

        val doujinName = file.name

        setSupportActionBar(toolbar)

        supportActionBar!!.title = doujinName
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun initReaderScreen() {
        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH)

        viewmodel.scanForImages(currentDir)

        val imageAdapter = ImageFragmentStateAdapter(supportFragmentManager)
        viewpager.adapter = imageAdapter

        viewmodel.imageList().observe(this, Observer {
            imageAdapter.setList(it)
            viewpager.currentItem = imagePositionInList
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

        viewmodel.imageList().observe(this, Observer {
            adapter.setList(it)

            linearLayoutManager.scrollToPosition(imagePositionInList)
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
        val pageNumber = "Page: ${number} / ${viewmodel.totalImageCount}"
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
