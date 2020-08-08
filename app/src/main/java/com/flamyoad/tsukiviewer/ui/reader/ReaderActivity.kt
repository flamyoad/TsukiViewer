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
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.BottomThumbnailAdapter
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.adapter.ReaderImageAdapter
import kotlinx.android.synthetic.main.activity_reader.*
import java.io.File


class ReaderActivity : AppCompatActivity(), BottomThumbnailAdapter.OnItemClickListener {

    private lateinit var viewmodel: ReaderViewModel

    private var currentAdapterPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        viewmodel = ViewModelProvider(this).get(ReaderViewModel::class.java)

        currentAdapterPosition = intent.getIntExtra(DoujinImagesAdapter.ADAPTER_POSITION, 0)

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

        val readerAdapter = ReaderImageAdapter()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = PagerSnapHelper()

        listImages.adapter = readerAdapter
        listImages.layoutManager = linearLayoutManager
        snapHelper.attachToRecyclerView(listImages)

        viewmodel.imageList.observe(this, Observer {
            readerAdapter.setList(it)
            linearLayoutManager.scrollToPosition(currentAdapterPosition)
        })
    }

    private fun initBottomThumbnails() {
        val adapter = BottomThumbnailAdapter(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = LinearSnapHelper()

        bottomListThumbnails.adapter = adapter
        bottomListThumbnails.layoutManager = linearLayoutManager
        snapHelper.attachToRecyclerView(bottomListThumbnails)

        viewmodel.imageList.observe(this, Observer {
            adapter.setList(it)

            linearLayoutManager.scrollToPosition(currentAdapterPosition)
        })
    }

    private fun hideStatusBar() {
        // Hides the status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        supportActionBar?.hide()
    }

    private fun initPageIndicator() {
        listImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

                val currentPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()

                // IF -1 is returned, means the user is partially swiping the item. No item is completely visible at this point
                // So we do not change the page number indicator
                if (currentPosition == RecyclerView.NO_POSITION) {

                    // Hides bottom sheet when the user scrolls, but not during scrolling done programmatically
                    toggleBottomSheet(View.INVISIBLE)
                    return
                }

                // Setting up the page number textview. Example: Page 11 / 20
                val pageNumber = "Page: ${currentPosition + 1} / ${viewmodel.totalImageCount}"
                txtCurrentPageNumber.text = pageNumber

                // Syncs the bottom thumbnail bar with the current page number
                val thumbnailLayoutManager =
                    bottomListThumbnails.layoutManager as LinearLayoutManager
                thumbnailLayoutManager.scrollToPosition(currentPosition + 1)
            }
        })

        bottomSheetOpener.setOnClickListener {
            toggleBottomSheet(View.VISIBLE)
        }
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
        val readerLayoutManager = listImages.layoutManager as LinearLayoutManager

        readerLayoutManager.scrollToPosition(adapterPosition)

        val thumbnailLayoutManager = bottomListThumbnails.layoutManager as LinearLayoutManager

        thumbnailLayoutManager.scrollToPosition(adapterPosition)
    }
}
