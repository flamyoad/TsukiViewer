package com.flamyoad.tsukiviewer.ui.reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.ReaderImageAdapter
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabViewModel
import kotlinx.android.synthetic.main.fragment_vertical_strip_reader.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class VerticalStripReaderFragment : Fragment() {
    private val viewModel: ReaderTabViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private val imageAdapter = ReaderImageAdapter()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val keyCode = intent?.getIntExtra(KEY_CODE, 0)
            if (keyCode == 0) return

            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    handleVolumeKey(viewModel.volumeUpAction)
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    handleVolumeKey(viewModel.volumeDownAction)
                }
            }
        }
    }

    private var readerListener: ReaderListener? = null

    private var viewPagerListener: ViewPagerListener? = null

    private var scrollJob: Job? = null

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vertical_strip_reader, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Setting up broadcast manager for volume key clicks
        if (!viewModel.shouldScrollWithVolumeButton) {
            return
        }

        LocalBroadcastManager
            .getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter(MY_KEY_DOWN_INTENT))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadcastReceiver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewPagerListener = context as ViewPagerListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try {
            readerListener = parentFragment as ReaderListener
        } catch (e: Exception) {
        }

        /* This used to restore reader position on screen rotation
           onRestoreInstanceState wouldn't work since the fragment gets recreated multiple times.
           First time it has value, but on subsequent calls the {savedInstanceState} is null
        */
        val readerPosition = when (viewModel.currentScrolledPosition != -1) {
            true -> viewModel.currentScrolledPosition
            false -> arguments?.getInt(POSITION_BEFORE_OPENING_READER, 0) ?: 0
        }

        initReader(readerPosition)
        setupPageIndicator(readerPosition)

        listImages.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        viewPagerListener?.setUserInputEnabled(false)
                    }
                    MotionEvent.ACTION_UP -> {
                        viewPagerListener?.setUserInputEnabled(true)
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        viewPagerListener?.setUserInputEnabled(true)
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        viewModel.bottomThumbnailSelectedItem().observe(viewLifecycleOwner, Observer {
            if (!this::layoutManager.isInitialized) return@Observer
            if (it == -1) return@Observer
            scrollTo(readerPosition)
        })
    }

    private fun initReader(readerPosition: Int) {
        val currentDir = arguments?.getString(CURRENT_DIR) ?: ""
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listImages.adapter = imageAdapter
        listImages.layoutManager = layoutManager
        listImages.setHasFixedSize(true)

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            imageAdapter.setList(it)

//            linearLayoutManager.scrollToPosition(readerPosition)
            scrollTo(readerPosition)

            readerListener?.onPageChange(readerPosition)

            viewModel.currentPath = currentDir
        })
    }

    private fun setupPageIndicator(readerPosition: Int) {
//        val currentPage = viewModel.currentImagePosition
        readerListener?.onPageChange(readerPosition)

        listImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) {
                    readerListener?.toggleBottomSheet(View.INVISIBLE)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastCompletelyVisible =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                val firstVisible = layoutManager.findFirstVisibleItemPosition()

                if (lastCompletelyVisible != RecyclerView.NO_POSITION) {
                    readerListener?.onPageChange(lastCompletelyVisible)
                } else {
                    readerListener?.onPageChange(firstVisible)
                }

                viewModel.currentScrolledPosition = firstVisible
            }
        })
    }

    // Cannot use normal kind of {LinearLayoutManager.scrollToPosition()} because the child height is wrap_content
    // So we have to keep scrolling and keep checking whether we have reached the position we want
    private fun scrollTo(position: Int) {
        scrollJob?.cancel()
        scrollJob = lifecycleScope.launchWhenResumed {
            val a = layoutManager.findLastVisibleItemPosition()
            val b = layoutManager.findFirstVisibleItemPosition()
            val hasReachedLastItem = layoutManager.findLastVisibleItemPosition() == position
            val hasReachedTargetItem = layoutManager.findFirstVisibleItemPosition() == position

            while (!(hasReachedLastItem || hasReachedTargetItem)) {
                layoutManager.scrollToPosition(position)
                delay(100)
            }
            viewModel.resetBottomThumbnailState()
        }
    }

    private fun handleVolumeKey(scrollDirection: VolumeButtonScrollDirection) {
        // Hides the bottom sheet when scrolling with volume button
        readerListener?.toggleBottomSheet(View.GONE)

        when (scrollDirection) {
            VolumeButtonScrollDirection.GoToPrevPage -> {
                when (viewModel.scrollingMode) {
                    VolumeButtonScrollMode.PageByPage -> {
                        scrollToPrevItem()
                    }

                    VolumeButtonScrollMode.FixedDistance -> {
                        listImages?.scrollBy(0, viewModel.scrollDistance.unaryMinus())
                    }
                }
            }

            VolumeButtonScrollDirection.GoToNextPage -> {
                when (viewModel.scrollingMode) {
                    VolumeButtonScrollMode.PageByPage -> {
                        scrollToNextItem()
                    }

                    VolumeButtonScrollMode.FixedDistance -> {
                        listImages?.scrollBy(0, viewModel.scrollDistance)
                    }
                }
            }

        }
    }

    private fun scrollToNextItem() {
        if (this::layoutManager.isInitialized) {
            val lastCompletelyVisible = layoutManager.findLastCompletelyVisibleItemPosition()
            val firstVisible = layoutManager.findFirstVisibleItemPosition()

            // Offset has to be 0, if not it scrolls to the middle of the item
            if (lastCompletelyVisible != RecyclerView.NO_POSITION) {
                layoutManager.scrollToPosition(lastCompletelyVisible + 1)
            } else {
                layoutManager.scrollToPosition(firstVisible + 1)
            }
        }
    }

    private fun scrollToPrevItem() {
        if (this::layoutManager.isInitialized) {
            val firstCompletelyVisible =
                layoutManager.findFirstCompletelyVisibleItemPosition()
            val lastVisible = layoutManager.findLastVisibleItemPosition()

            // Offset has to be 0, if not it scrolls to the middle of the item
            if (firstCompletelyVisible != RecyclerView.NO_POSITION) {
                layoutManager.scrollToPosition(firstCompletelyVisible - 1)
            } else {
                layoutManager.scrollToPosition(lastVisible - 1)
            }
        }
    }


    companion object {
        const val CURRENT_DIR = "current_dir"
        const val READER_POSITION = "reader_position"
        const val POSITION_BEFORE_OPENING_READER = "position_before_opening_reader"

        @JvmStatic
        fun newInstance(currentDir: String, positionBeforeOpenReader: Int) =
            VerticalStripReaderFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_DIR, currentDir)
                    putInt(POSITION_BEFORE_OPENING_READER, positionBeforeOpenReader)
                }
            }
    }
}
