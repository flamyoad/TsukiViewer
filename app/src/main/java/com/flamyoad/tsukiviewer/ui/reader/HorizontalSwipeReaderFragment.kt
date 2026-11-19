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
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import com.flamyoad.tsukiviewer.core.model.ReaderSpreadPreferences
import com.flamyoad.tsukiviewer.core.model.ReaderSpreads
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference
import com.flamyoad.tsukiviewer.databinding.FragmentHorizontalReaderBinding
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabViewModel
import java.io.File

class HorizontalSwipeReaderFragment : Fragment() {
    private var _binding: FragmentHorizontalReaderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReaderTabViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private var readerListener: ReaderListener? = null

    private var anchorPage = 0
    private var images = emptyList<File>()
    private var spreads = ReaderSpreads(0, false, false, false)
    private var updatingLayout = false
    private val isTwoPageMode get() = arguments?.getBoolean(TWO_PAGE_MODE) == true

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val direction = when (intent?.getIntExtra(KEY_CODE, 0)) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> viewModel.volumeDownAction
                KeyEvent.KEYCODE_VOLUME_UP -> viewModel.volumeUpAction
                else -> return
            }
            readerListener?.toggleBottomSheet(View.GONE)
            val step = when (direction) {
                VolumeButtonScrollDirection.GoToNextPage -> 1
                VolumeButtonScrollDirection.GoToPrevPage -> -1
                VolumeButtonScrollDirection.Nothing -> return
            }
            val pager = binding.viewpager
            pager.setCurrentItem(pager.currentItem + if (spreads.rightToLeft) -step else step, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHorizontalReaderBinding.inflate(inflater, container, false)
        if (isTwoPageMode) {
            binding.viewpager.updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = 0 }
        }
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

            if (it in images.indices) {
                anchorPage = it
                binding.viewpager.setCurrentItem(spreads.itemForPage(it), false)
                publishPage(binding.viewpager.currentItem)
            }
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
        binding.viewpager.isSaveEnabled = false

        val currentDir = arguments?.getString(CURRENT_DIR) ?: ""

        anchorPage = viewModel.currentScrolledPosition.takeIf { it >= 0 }
            ?: arguments?.getInt(POSITION_BEFORE_OPENING_READER, 0) ?: 0

        binding.viewpager.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            applyLayout()
        }
        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            val changed = images != it
            images = it
            applyLayout(force = changed)
            viewModel.currentPath = currentDir
        })

        binding.viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) = publishPage(position)

            override fun onPageScrollStateChanged(state: Int) {
                if (state == SCROLL_STATE_DRAGGING) {
                    readerListener?.toggleBottomSheet(View.INVISIBLE)
                }
            }
        })
    }

    private fun applyLayout(force: Boolean = false) {
        if (images.isEmpty()) return
        val preferences = ReaderSpreadPreferences.read(MyAppPreference.getInstance(requireContext()).prefs)
        val window = resources.configuration
        val layout = ReaderSpreads(
            images.size,
            preferences.useSpreads(
                window.screenWidthDp,
                window.screenHeightDp,
                explicitlySelected = isTwoPageMode
            ),
            preferences.separateCover,
            preferences.rightToLeft
        )
        if (!force && layout == spreads) return

        val pager = binding.viewpager
        updatingLayout = true
        spreads = layout
        anchorPage = anchorPage.coerceIn(images.indices)
        // A fresh adapter keeps saved fragments from the previous pairing out of the new spread.
        pager.adapter = ImageFragmentStateAdapter(childFragmentManager, images, layout)
        pager.setCurrentItem(layout.itemForPage(anchorPage), false)
        updatingLayout = false
        publishPage(pager.currentItem)
    }

    private fun publishPage(item: Int) {
        if (updatingLayout || item !in 0 until spreads.itemCount) return
        val pages = spreads.pagesForItem(item)
        if (anchorPage !in pages) anchorPage = pages.first
        readerListener?.onPageSpreadChange(anchorPage, pages)
    }

    private fun setupBroadcastReceiver() {
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter(MY_KEY_DOWN_INTENT))
    }

    companion object {
        private const val TWO_PAGE_MODE = "two_page_mode"
        const val CURRENT_DIR = "current_dir"
        const val POSITION_BEFORE_OPENING_READER = "position_before_opening_reader"

        @JvmStatic
        fun newInstance(currentDir: String, positionBeforeOpenReader: Int, twoPage: Boolean = false) =
            HorizontalSwipeReaderFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_DIR, currentDir)
                    putBoolean(TWO_PAGE_MODE, twoPage)
                    putInt(POSITION_BEFORE_OPENING_READER, positionBeforeOpenReader)
                }
            }
    }
}
