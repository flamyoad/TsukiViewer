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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.ReaderImageAdapter
import kotlinx.android.synthetic.main.fragment_vertical_strip_reader.*

class VerticalStripReaderFragment : Fragment() {
    private val viewModel: ReaderViewModel by activityViewModels()

    private var listener: ReaderListener? = null

    private val imageAdapter = ReaderImageAdapter()

    private lateinit var linearLayoutManager: LinearLayoutManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vertical_strip_reader, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ReaderListener
        } catch (ignored: ClassCastException) {
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initReader()
        setupPageIndicator()
        setupBroadcastReceiver()

        viewModel.bottomThumbnailSelectedItem().observe(viewLifecycleOwner, Observer {
            if (!this::linearLayoutManager.isInitialized) return@Observer
            if (it == -1) return@Observer

            linearLayoutManager.scrollToPosition(it)

            viewModel.resetBottomThumbnailState()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadcastReceiver)
    }

    private fun initReader() {
        val currentDir = arguments?.getString(CURRENT_DIR) ?: ""
        val positionFromImageGrid = arguments?.getInt(POSITION_BEFORE_OPENING_READER, 0) ?: 0

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listImages.adapter = imageAdapter
        listImages.layoutManager = linearLayoutManager
        listImages.setHasFixedSize(true)

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            imageAdapter.setList(it)

            if (viewModel.currentPath.isBlank()) {
                linearLayoutManager.scrollToPosition(positionFromImageGrid)
                listener?.onPageChange(positionFromImageGrid)
            } else {
                linearLayoutManager.scrollToPosition(viewModel.currentImagePosition)
            }

            viewModel.currentPath = currentDir
        })
    }

    private fun setupPageIndicator() {
        val currentPage = viewModel.currentImagePosition
        listener?.onPageChange(currentPage)

        listImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) {
                    listener?.toggleBottomSheet(View.INVISIBLE)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastCompletelyVisible =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val firstVisible = linearLayoutManager.findFirstVisibleItemPosition()

                if (lastCompletelyVisible != RecyclerView.NO_POSITION) {
                    listener?.onPageChange(lastCompletelyVisible)
                } else {
                    listener?.onPageChange(firstVisible)
                }
            }
        })
    }

    private fun setupBroadcastReceiver() {
        if (!viewModel.shouldScrollWithVolumeButton) {
            return
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter(KEY_DOWN_INTENT))
    }

    private fun handleVolumeKey(scrollDirection: VolumeButtonScrollDirection) {
        // Hides the bottom sheet when scrolling with volume button
        listener?.toggleBottomSheet(View.GONE)

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
        if (this::linearLayoutManager.isInitialized) {
            val lastCompletelyVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val firstVisible = linearLayoutManager.findFirstVisibleItemPosition()

            // Offset has to be 0, if not it scrolls to the middle of the item
            if (lastCompletelyVisible != RecyclerView.NO_POSITION) {
                linearLayoutManager.scrollToPositionWithOffset(lastCompletelyVisible + 1, 0)
            } else {
                linearLayoutManager.scrollToPositionWithOffset(firstVisible + 1, 0)
            }
        }
    }

    private fun scrollToPrevItem() {
        if (this::linearLayoutManager.isInitialized) {
            val firstCompletelyVisible =
                linearLayoutManager.findFirstCompletelyVisibleItemPosition()
            val lastVisible = linearLayoutManager.findLastVisibleItemPosition()

            // Offset has to be 0, if not it scrolls to the middle of the item
            if (firstCompletelyVisible != RecyclerView.NO_POSITION) {
                linearLayoutManager.scrollToPositionWithOffset(firstCompletelyVisible - 1, 0)
            } else {
                linearLayoutManager.scrollToPositionWithOffset(lastVisible - 1, 0)
            }
        }
    }


    companion object {
        const val CURRENT_DIR = "current_dir"
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
