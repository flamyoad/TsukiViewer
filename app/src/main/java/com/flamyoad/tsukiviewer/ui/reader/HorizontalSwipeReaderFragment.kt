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
import com.flamyoad.tsukiviewer.databinding.FragmentHorizontalReaderBinding
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabViewModel

class HorizontalSwipeReaderFragment : Fragment() {
    private var _binding: FragmentHorizontalReaderBinding? = null
    private val binding get() = _binding!!

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
                        VolumeButtonScrollDirection.GoToNextPage -> binding.viewpager.arrowScroll(View.FOCUS_RIGHT)
                        VolumeButtonScrollDirection.GoToPrevPage -> binding.viewpager.arrowScroll(View.FOCUS_LEFT)
                    }
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    readerListener?.toggleBottomSheet(View.GONE)

                    when (viewModel.volumeUpAction) {
                        VolumeButtonScrollDirection.GoToNextPage -> binding.viewpager.arrowScroll(View.FOCUS_RIGHT)
                        VolumeButtonScrollDirection.GoToPrevPage -> binding.viewpager.arrowScroll(View.FOCUS_LEFT)
                    }
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHorizontalReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try {
            readerListener = parentFragment as ReaderListener
        } catch (e: Exception) {
        }

        initReader()
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
        _binding = null
    }

    private fun initReader() {
        binding.viewpager.offscreenPageLimit = 1

        val currentDir = arguments?.getString(CURRENT_DIR) ?: ""

        val readerPosition = when (viewModel.currentScrolledPosition != -1) {
            true -> viewModel.currentScrolledPosition
            false -> arguments?.getInt(POSITION_BEFORE_OPENING_READER, 0) ?: 0
        }

        val imageAdapter = ImageFragmentStateAdapter(childFragmentManager)
        binding.viewpager.adapter = imageAdapter

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            imageAdapter.setList(it)

            binding.viewpager.setCurrentItem(readerPosition, false)
            readerListener?.onPageChange(readerPosition)

            viewModel.currentPath = currentDir
        })

        binding.viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                viewModel.currentScrolledPosition = position
            }
            override fun onPageSelected(position: Int) { // For page indicator
                readerListener?.onPageChange(position)
            }
            override fun onPageScrollStateChanged(state: Int) { // For page indicator
                if (state == SCROLL_STATE_DRAGGING) {
                    readerListener?.toggleBottomSheet(View.INVISIBLE)
                }
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
