package com.flamyoad.tsukiviewer.ui.reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabViewModel
import kotlinx.android.synthetic.main.fragment_swipe_reader.*

class HorizontalSwipeReaderFragment : Fragment() {
    private val viewModel: ReaderTabViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private var readerListener: ReaderListener? = null

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val keyCode = intent?.getIntExtra(KEY_CODE, 0)
            if (keyCode == 0) return

            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    readerListener?.toggleBottomSheet(View.GONE) // Hides the bottom sheet when scrolling with volume button

                    when (viewModel.volumeDownAction) {
                        VolumeButtonScrollDirection.GoToNextPage -> viewpager?.arrowScroll(View.FOCUS_RIGHT)
                        VolumeButtonScrollDirection.GoToPrevPage -> viewpager?.arrowScroll(View.FOCUS_LEFT)
                    }
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    readerListener?.toggleBottomSheet(View.GONE)

                    when (viewModel.volumeDownAction) {
                        VolumeButtonScrollDirection.GoToNextPage -> viewpager?.arrowScroll(View.FOCUS_RIGHT)
                        VolumeButtonScrollDirection.GoToPrevPage -> viewpager?.arrowScroll(View.FOCUS_LEFT)
                    }
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swipe_reader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try {
            readerListener = parentFragment as ReaderListener
        } catch (e: Exception) { }

        initReader()
        setupPageIndicator()
        setupBroadcastReceiver()

        viewModel.bottomThumbnailSelectedItem().observe(viewLifecycleOwner, Observer {
            if (it == -1) return@Observer

            viewpager.setCurrentItem(it, false)
            viewModel.resetBottomThumbnailState()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadcastReceiver)
    }

    private fun initReader() {
        viewpager.offscreenPageLimit = 1

        val currentDir = arguments?.getString(CURRENT_DIR) ?: ""
        val positionFromImageGrid = arguments?.getInt(POSITION_BEFORE_OPENING_READER, 0) ?: 0

        val imageAdapter = ImageFragmentStateAdapter(childFragmentManager)
        viewpager.adapter = imageAdapter

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            imageAdapter.setList(it)

            if (viewModel.currentPath.isBlank()) {
                viewpager.setCurrentItem(positionFromImageGrid, false)
                readerListener?.onPageChange(positionFromImageGrid)
            } else {
                viewpager.setCurrentItem(viewModel.currentImagePosition)
            }

            viewModel.currentPath = currentDir
        })
    }

    private fun setupPageIndicator() {
        val currentPage = viewModel.currentImagePosition
        readerListener?.onPageChange(currentPage)

        viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == SCROLL_STATE_DRAGGING) {
                    readerListener?.toggleBottomSheet(View.INVISIBLE)
                }
            }

            override fun onPageSelected(position: Int) {
                readerListener?.onPageChange(position)
            }
        })
    }

    private fun setupBroadcastReceiver() {
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter(MY_KEY_DOWN_INTENT))
    }

    companion object {
        const val CURRENT_DIR = "current_dir"
        const val POSITION_BEFORE_OPENING_READER = "position_before_opening_reader"

        @JvmStatic
        fun newInstance(currentDir: String, positionBeforeOpenReader: Int) =
            HorizontalSwipeReaderFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_DIR, currentDir)
                    putInt(POSITION_BEFORE_OPENING_READER, positionBeforeOpenReader)
                }
            }
    }
}
