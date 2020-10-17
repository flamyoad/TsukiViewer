package com.flamyoad.tsukiviewer.ui.reader

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import com.flamyoad.tsukiviewer.R
import kotlinx.android.synthetic.main.fragment_swipe_reader.*

class SwipeReaderFragment : Fragment() {
    private val viewModel: ReaderViewModel by activityViewModels()

    private var listener: ReaderListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swipe_reader, container, false)
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

        viewModel.bottomThumbnailSelectedItem().observe(viewLifecycleOwner, Observer {
            viewpager.setCurrentItem(it, false)
        })
    }

    private fun initReader() {
        viewpager.offscreenPageLimit = 2

        val currentDir = arguments?.getString(CURRENT_DIR) ?: ""
        val positionFromImageGrid = arguments?.getInt(POSITION_BEFORE_OPENING_READER, 0) ?: 0

        val imageAdapter = ImageFragmentStateAdapter(childFragmentManager)
        viewpager.adapter = imageAdapter

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            imageAdapter.setList(it)
            if (viewModel.currentPath.isBlank()) {
                viewpager.setCurrentItem(positionFromImageGrid, false)
                listener?.onPageChange(positionFromImageGrid)
            }
            viewModel.currentPath = currentDir
        })
    }

    private fun setupPageIndicator() {
        val currentPage = viewModel.currentImagePosition
        listener?.onPageChange(currentPage)

        viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == SCROLL_STATE_DRAGGING) {
                    listener?.toggleBottomSheet(View.INVISIBLE)
                }
            }

            override fun onPageSelected(position: Int) {
                listener?.onPageChange(position)
            }
        })
    }

    companion object {
        const val CURRENT_DIR = "current_dir"
        const val POSITION_BEFORE_OPENING_READER = "position_before_opening_reader"

        @JvmStatic
        fun newInstance(currentDir: String, positionBeforeOpenReader: Int) =
            SwipeReaderFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_DIR, currentDir)
                    putInt(POSITION_BEFORE_OPENING_READER, positionBeforeOpenReader)
                }
            }
    }
}
