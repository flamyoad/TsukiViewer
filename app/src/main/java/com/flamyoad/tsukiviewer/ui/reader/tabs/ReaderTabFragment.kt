package com.flamyoad.tsukiviewer.ui.reader.tabs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.BottomThumbnailAdapter
import com.flamyoad.tsukiviewer.model.RecentTab
import com.flamyoad.tsukiviewer.ui.reader.*
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity.Companion.RECENT_TAB_REQUEST_CODE
import com.flamyoad.tsukiviewer.ui.reader.recents.RecentTabsActivity
import com.flamyoad.tsukiviewer.utils.extensions.toast
import kotlinx.android.synthetic.main.fragment_reader_tab.*
import java.io.File

private const val SWIPE_READER = "swipe_reader"

class ReaderTabFragment : Fragment(),
    ReaderListener,
    BottomThumbnailAdapter.OnItemClickListener {

    private val viewModel: ReaderTabViewModel by viewModels()

    private var viewPagerListener: ViewPagerListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reader_tab, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewPagerListener = context as ViewPagerListener
    }

    override fun onResume() {
        super.onResume()
        viewPagerListener?.setUserInputEnabled(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.directoryNoLongerExists().observe(viewLifecycleOwner, Observer { notExists ->
            if (notExists) {
                toast("Directory not found. Renamed or deleted?")
//                finish()
            }
        })

        toolbarContent.setOnTouchListener { view, motionEvent ->
            viewPagerListener?.setUserInputEnabled(true)
            return@setOnTouchListener false
        }

        initBottomThumbnails()
        initPageIndicator()
        initToolbar()

        viewModel.readerMode().observe(viewLifecycleOwner, Observer {
            setupReader(it)
            setupSideMenu(it)
        })

        viewModel.recentTabs.observe(viewLifecycleOwner, Observer {
            btnTab.text = it.size.toString()
        })

        btnHorizReader.setOnClickListener {
            viewModel.setReaderMode(ReaderMode.HorizontalSwipe)
        }

        btnVertReader.setOnClickListener {
            viewModel.setReaderMode(ReaderMode.VerticalStrip)
        }

        btnTab.setOnClickListener {
            val intent = Intent(requireContext(), RecentTabsActivity::class.java)
            requireActivity().startActivityForResult(intent, RECENT_TAB_REQUEST_CODE)
        }
    }

    private fun setupReader(mode: ReaderMode) {
        val currentDir = arguments?.getString(DIR_PATH) ?: ""
        val positionInGrid = arguments?.getInt(STARTING_IMAGE_POSITION, 0) ?: 0

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

        childFragmentManager.beginTransaction()
            .replace(R.id.imageContainer, fragment, SWIPE_READER)
            .commit()
    }

    private fun setupSideMenu(mode: ReaderMode) {
        // Changes button color according to active/inactive state
        val activeBtnColor = ContextCompat.getColor(
            requireContext(),
            R.color.read_mode_button_active
        )
        val inactiveBtnColor = ContextCompat.getColor(
            requireContext(),
            R.color.read_mode_button_inactive
        )

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

    private fun initBottomThumbnails() {
        val adapter = BottomThumbnailAdapter(this)

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = LinearSnapHelper()

        bottomListThumbnails.adapter = adapter
        bottomListThumbnails.layoutManager = linearLayoutManager
        snapHelper.attachToRecyclerView(bottomListThumbnails)

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            linearLayoutManager.scrollToPosition(0)
        })
    }

    private fun initPageIndicator() {
        bottomSheetOpener.setOnClickListener {
            toggleBottomSheet(View.VISIBLE)
        }
    }

    private fun initToolbar() {
        val path = arguments?.getString(DIR_PATH) ?: ""
        val dir = File(path)
        toolbarTitle.text = dir.name
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

//        when (visibility) {
//            View.VISIBLE -> supportActionBar?.show()
//            else -> supportActionBar?.hide()
//        }
    }

    override fun onPageChange(pageNum: Int) {
        viewModel.currentImagePosition = pageNum
        setPageIndicatorNumber(pageNum + 1)

        val bottomLayoutManager = bottomListThumbnails.layoutManager as LinearLayoutManager
        bottomLayoutManager.scrollToPosition(pageNum)
    }

    override fun onThumbnailClick(adapterPosition: Int) {
        viewModel.onThumbnailClick(adapterPosition)

        val thumbnailLayoutManager = bottomListThumbnails.layoutManager as LinearLayoutManager
        thumbnailLayoutManager.scrollToPosition(adapterPosition)

        hideReaderModeDialog()
    }

    private fun hideReaderModeDialog() {
        val rightSlide = Slide(Gravity.END).apply {
            addTarget(R.id.readerModeDialog)
        }
        TransitionManager.beginDelayedTransition(readerModeDialog, rightSlide)
        readerModeDialog.visibility = View.GONE
    }

    private fun setPageIndicatorNumber(pageNum: Int) {
        val pageNumber = "Page: ${pageNum} / ${viewModel.getTotalImagesCount()}"
        txtCurrentPageNumber.text = pageNumber
    }

    companion object {
        const val DIR_PATH = "dir_path"
        const val STARTING_IMAGE_POSITION = "starting_image_position"

        @JvmStatic
        fun newInstance(tab: RecentTab, startPosition: Int) = ReaderTabFragment().apply {
            arguments = Bundle().apply {
                putString(DIR_PATH, tab.dirPath.absolutePath)
                putInt(STARTING_IMAGE_POSITION, startPosition)
            }
        }
    }

}
