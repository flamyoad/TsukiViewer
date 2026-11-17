package com.flamyoad.tsukiviewer.ui.reader.tabs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.BottomThumbnailAdapter
import com.flamyoad.tsukiviewer.databinding.FragmentReaderTabBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.core.model.RecentTab
import com.flamyoad.tsukiviewer.ui.reader.*
import com.flamyoad.tsukiviewer.utils.extensions.toast
import java.io.File
import javax.inject.Inject

private const val SWIPE_READER = "swipe_reader"

class ReaderTabFragment : Fragment(),
    ReaderListener,
    BottomThumbnailAdapter.OnItemClickListener {

    private var _binding: FragmentReaderTabBinding? = null
    private val binding get() = requireNotNull(_binding)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: ReaderTabViewModel by viewModels { viewModelFactory }
    private val parentViewModel: ReaderViewModel by activityViewModels { viewModelFactory }

    private var viewPagerListener: ViewPagerListener? = null
    private var readerListener: ReaderTabListener? = null
    private var lastReadPageNumberListener: LastReadPageNumberListener? = null
    private var shouldReturnLastReadPosition: Boolean = false
    private var cutoutTop = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
        viewPagerListener = context as ViewPagerListener
        readerListener = context as ReaderTabListener
        lastReadPageNumberListener = context as LastReadPageNumberListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldReturnLastReadPosition = arguments?.getBoolean(RETURN_LAST_READ_POSITION) ?: false
        if (viewModel.currentScrolledPosition < 0) {
            viewModel.currentScrolledPosition = savedInstanceState?.getInt(SAVED_PAGE, -1) ?: -1
        }
    }

    override fun onPause() {
        viewModel.savePage(arguments?.getString(DIR_PATH).orEmpty())
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_PAGE, viewModel.currentScrolledPosition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.directoryNoLongerExists().observe(viewLifecycleOwner, Observer { notExists ->
            if (notExists) {
                toast("Directory not found. Renamed or deleted?")
            }
        })

        initBottomThumbnails()
        initPageIndicator()
        initToolbar()
        keepToolbarBelowCutout()

        viewModel.recentTabs.observe(viewLifecycleOwner, Observer {
            binding.btnTab.text = it.size.toString()
            binding.btnTab.contentDescription = resources.getQuantityString(
                R.plurals.reader_tabs_show, it.size, it.size
            )
        })

        parentViewModel.readerMode().observe(viewLifecycleOwner, Observer {
            setupReader(it)
            setupSideMenu(it)
        })

        binding.btnHorizontalSwipe.setOnClickListener {
            parentViewModel.setReaderMode(ReaderMode.HorizontalSwipe)
        }

        binding.btnVerticalSwipe.setOnClickListener {
            parentViewModel.setReaderMode(ReaderMode.VerticalSwipe)
        }

        binding.btnVerticalStrip.setOnClickListener {
            parentViewModel.setReaderMode(ReaderMode.VerticalStrip)
        }

        binding.btnTab.setOnClickListener {
            readerListener?.openTabSwitcher()
        }

        binding.btnBack.setOnClickListener {
            readerListener?.quitActivity()
        }
    }

    private fun keepToolbarBelowCutout() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, insets ->
            cutoutTop = insets.getInsets(WindowInsetsCompat.Type.displayCutout()).top
            val page = viewModel.currentScrolledPosition.takeIf { it >= 0 }
                ?: arguments?.getInt(STARTING_IMAGE_POSITION, 0) ?: 0
            updateToolbarCutoutMargin(page)
            insets
        }
        ViewCompat.requestApplyInsets(binding.appBarLayout)
    }

    private fun updateToolbarCutoutMargin(page: Int) {
        val toolbar = binding.toolbar
        val topMargin = if (page == 0) cutoutTop else 0
        if ((toolbar.layoutParams as ViewGroup.MarginLayoutParams).topMargin != topMargin) {
            toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = topMargin
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupReader(mode: ReaderMode) {
        val currentDir = arguments?.getString(DIR_PATH) ?: ""
        val positionInGrid = viewModel.currentScrolledPosition.takeIf { it >= 0 }
            ?: arguments?.getInt(STARTING_IMAGE_POSITION, -1)?.takeIf { it >= 0 }
            ?: viewModel.savedPage(currentDir)

        viewModel.scanForImages(currentDir)

        val fragment = when (mode) {
            ReaderMode.HorizontalSwipe -> HorizontalSwipeReaderFragment.newInstance(
                currentDir,
                positionInGrid
            )
            ReaderMode.VerticalSwipe -> VerticalSwipeReaderFragment.newInstance(
                currentDir,
                positionInGrid
            )
            ReaderMode.VerticalStrip -> VerticalStripReaderFragment.newInstance(
                currentDir,
                positionInGrid
            )
        }

        if (mode == ReaderMode.HorizontalSwipe || mode == ReaderMode.VerticalSwipe) {
            binding.appBarLayout.setExpanded(true)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.imageContainer, fragment, SWIPE_READER)
            .commit()
    }

    // Changes button color according to active/inactive state
    private fun setupSideMenu(mode: ReaderMode) {
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
                binding.btnHorizontalSwipe.background.setTint(activeBtnColor)
                binding.btnVerticalSwipe.background.setTint(inactiveBtnColor)
                binding.btnVerticalStrip.background.setTint(inactiveBtnColor)
            }
            ReaderMode.VerticalSwipe -> {
                binding.btnHorizontalSwipe.background.setTint(inactiveBtnColor)
                binding.btnVerticalSwipe.background.setTint(activeBtnColor)
                binding.btnVerticalStrip.background.setTint(inactiveBtnColor)
            }
            ReaderMode.VerticalStrip -> {
                binding.btnVerticalStrip.background.setTint(activeBtnColor)
                binding.btnVerticalSwipe.background.setTint(inactiveBtnColor)
                binding.btnHorizontalSwipe.background.setTint(inactiveBtnColor)
            }
        }
    }

    private fun initBottomThumbnails() {
        val adapter = BottomThumbnailAdapter(this)

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = LinearSnapHelper()

        binding.bottomListThumbnails.adapter = adapter
        binding.bottomListThumbnails.layoutManager = linearLayoutManager
        snapHelper.attachToRecyclerView(binding.bottomListThumbnails)

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            linearLayoutManager.scrollToPosition(0)
        })

        binding.bottomListThumbnails.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
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
    }

    private fun initPageIndicator() {
        binding.bottomSheetOpener.setOnClickListener {
            toggleBottomSheet(View.VISIBLE)
        }
    }

    private fun initToolbar() {
        val path = arguments?.getString(DIR_PATH) ?: ""
        val dir = File(path)
        binding.toolbarTitle.text = dir.name
    }

    override fun toggleBottomSheet(visibility: Int) {
        val btmSlide = Slide(Gravity.BOTTOM).apply {
            addTarget(R.id.bottomSheetDialog)
        }

        val rightSlide = Slide(Gravity.END).apply {
            addTarget(R.id.readerModeDialog)
        }

        TransitionManager.beginDelayedTransition(binding.bottomSheetDialog, btmSlide)
        TransitionManager.beginDelayedTransition(binding.readerModeDialog, rightSlide)

        binding.bottomSheetDialog.visibility = visibility
        binding.readerModeDialog.visibility = visibility
    }

    override fun onPageChange(pageNum: Int) {
        viewModel.currentScrolledPosition = pageNum
        if (pageNum >= 0) updateToolbarCutoutMargin(pageNum)
        setPageIndicatorNumber(pageNum + 1)

        val bottomLayoutManager = binding.bottomListThumbnails.layoutManager as LinearLayoutManager?
        bottomLayoutManager?.scrollToPosition(pageNum)

        if (shouldReturnLastReadPosition) {
            lastReadPageNumberListener?.savePageNumber(pageNum)
        }
    }

    override fun onThumbnailClick(adapterPosition: Int) {
        viewModel.onThumbnailClick(adapterPosition)

        val thumbnailLayoutManager = binding.bottomListThumbnails.layoutManager as LinearLayoutManager?
        thumbnailLayoutManager?.scrollToPosition(adapterPosition)

        hideReaderModeDialog()
    }

    private fun hideReaderModeDialog() {
        val rightSlide = Slide(Gravity.END).apply {
            addTarget(R.id.readerModeDialog)
        }
        TransitionManager.beginDelayedTransition(binding.readerModeDialog, rightSlide)
        binding.readerModeDialog.visibility = View.GONE
    }

    private fun setPageIndicatorNumber(pageNum: Int) {
        val pageNumber = "Page: ${pageNum} / ${viewModel.getTotalImagesCount()}"
        binding.txtCurrentPageNumber.text = pageNumber
    }

    companion object {
        const val DIR_PATH = "dir_path"
        private const val SAVED_PAGE = "saved_page"
        const val STARTING_IMAGE_POSITION = "starting_image_position"
        const val RETURN_LAST_READ_POSITION = "return_last_read_position"

        @JvmStatic
        fun newInstance(tab: RecentTab, startPosition: Int, returnLastReadPosition: Boolean) =
            ReaderTabFragment().apply {
                arguments = Bundle().apply {
                    putString(DIR_PATH, tab.dirPath.absolutePath)
                    putInt(STARTING_IMAGE_POSITION, startPosition)
                    putBoolean(RETURN_LAST_READ_POSITION, returnLastReadPosition)
                }
            }
    }

}
