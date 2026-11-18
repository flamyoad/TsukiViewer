package com.flamyoad.tsukiviewer.ui.reader

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.ImageView
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnPreDraw
import androidx.activity.viewModels
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.security.LockableActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.databinding.ActivityReaderBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.ui.reader.recents.RecentTabsActivity
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabFragment
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabFragmentAdapter
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabListener
import com.flamyoad.tsukiviewer.utils.extensions.reduceDragSensitivitySlightly
import com.flamyoad.tsukiviewer.utils.extensions.toast
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.math.abs


const val MY_KEY_DOWN_INTENT = "my_key_down_intent"
const val KEY_CODE = "key_code"

private typealias ReturnTarget = (Int, (ImageView?) -> Unit) -> Unit

class ReaderActivity : LockableActivity(), ViewPagerListener, ReaderTabListener, LastReadPageNumberListener {

    private lateinit var binding: ActivityReaderBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: ReaderViewModel by viewModels { viewModelFactory }

    private var positionFromImageGrid = 0
    private var returningSharedPage = false
    private val readyPages = mutableMapOf<String, WeakReference<SubsamplingScaleImageView>>()
    private var returnAnimation: ReaderPageReturnAnimation? = null
    private var openingPageName: String? = null
    private val showLoadingReader = Runnable { revealReader(animate = false) }
    private var transitionSource: WeakReference<ReturnTarget>? = null

    private var pendingTabId: Long? = null
    private var sidebarSwipeTab: ReaderTabFragment? = null
    private var sidebarSwipeDirection = 0
    private var sidebarSwipeStartX = 0f
    private var sidebarSwipeStartY = 0f
    private var sidebarSwipeClaimed = false
    private var sidebarSwipeClosing = false

    private lateinit var tabFragmentAdapter: ReaderTabFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.reader_no_window_animation, R.anim.reader_no_window_animation)
        }
        binding = ActivityReaderBinding.inflate(layoutInflater)
        configureFullscreen()
        setContentView(binding.root)
        if (savedInstanceState == null) {
            transitionSource = pendingSource
            pendingSource = null
        }
        if (savedInstanceState == null && transitionSource?.get() != null && !appLock.isLocked) {
            openingPageName = intent.getStringExtra(OPENING_PAGE)
            if (openingPageName != null) {
                binding.root.alpha = 0f
                binding.root.postDelayed(showLoadingReader, OPENING_PAGE_TIMEOUT_MS)
            }
        }
        viewModel.directoryNoLongerExists().observe(this, Observer { notExists ->
            if (notExists) {
                revealReader(animate = false)
                toast("Directory not found. Renamed or deleted?")
            }
        })
        positionFromImageGrid = intent.getIntExtra(
            DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER,
            0
        )

        if (savedInstanceState == null) {
            viewModel.lastReadImagePosition = positionFromImageGrid
        }

        val currentDir = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: ""
        pendingTabId = savedInstanceState?.getLong(SELECTED_TAB_ID, RecentTabsActivity.NO_TAB)
            ?.takeUnless { it == RecentTabsActivity.NO_TAB }

        setupViewPager(startPosition = positionFromImageGrid.takeIf { savedInstanceState == null })
        if (savedInstanceState == null && currentDir.isNotEmpty()) {
            viewModel.insertRecentTab(currentDir)
        }
        binding.viewPager.reduceDragSensitivitySlightly()
    }

    private fun configureFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= 28) {
            val attributes = window.attributes
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attributes
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.statusBars())
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val navigation = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            binding.viewPager.setPadding(
                maxOf(navigation.left, cutout.left),
                0,
                maxOf(navigation.right, cutout.right),
                maxOf(navigation.bottom, cutout.bottom)
            )
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                closeReader()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != RECENT_TAB_REQUEST_CODE || resultCode != RESULT_OK || data == null) return
        val tabId = data.getLongExtra(RecentTabsActivity.CHOSEN_TAB_ID, RecentTabsActivity.NO_TAB)
        if (tabId == RecentTabsActivity.NO_TAB) {
            closeReader()
            return
        }
        pendingTabId = tabId
        selectPendingTab()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val id = pendingTabId ?: tabFragmentAdapter.tabIdAt(binding.viewPager.currentItem)
        outState.putLong(SELECTED_TAB_ID, id ?: RecentTabsActivity.NO_TAB)
    }

    /* dispatchKeyEvent is preferred over onKeyDown
       This is because onKeyDown will still emit the volume click sound, whereas dispatchKeyEvent does not

       https://stackoverflow.com/questions/24121644/dispatchkeyevent-invoking-twice
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val keyCode = event?.keyCode
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (event.action != KeyEvent.ACTION_DOWN) return true

            val intent = Intent(MY_KEY_DOWN_INTENT)
            intent.putExtra(KEY_CODE, keyCode)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            return true
        } else {
            return super.dispatchKeyEvent(event)
        }
    }

    private fun setupViewPager(startPosition: Int?) {
        tabFragmentAdapter = ReaderTabFragmentAdapter(this)

        tabFragmentAdapter.setFirstItem(
            intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH) ?: "",
            startPosition
        )

        val pager = binding.viewPager
        (pager.getChildAt(0) as RecyclerView).layoutManager?.isItemPrefetchEnabled = false

        viewModel.recentTabs.observe(this) { tabs ->
            val previousPosition = pager.currentItem
            val previousId = tabFragmentAdapter.tabIdAt(previousPosition)
            val originalDirectory = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH).orEmpty()

            tabFragmentAdapter.setList(tabs)
            if (pager.adapter == null && tabs.isNotEmpty()) pager.adapter = tabFragmentAdapter
            if (tabs.isEmpty()) return@observe

            if (selectPendingTab()) return@observe
            val preferredId = previousId ?: tabFragmentAdapter.getTab(originalDirectory)?.id
            val position = preferredId?.let(tabFragmentAdapter::getTabPosition) ?: -1
            val nextPosition = if (position >= 0) position else previousPosition.coerceAtMost(tabs.lastIndex)
            pager.setCurrentItem(nextPosition, false)
        }

        viewModel.openedTabId.observe(this) { id ->
            pendingTabId = id ?: return@observe
            viewModel.clearOpenedTab()
            selectPendingTab()
        }
    }

    private fun selectPendingTab(): Boolean {
        val id = pendingTabId ?: return false
        val position = tabFragmentAdapter.getTabPosition(id)
        if (position < 0 || binding.viewPager.adapter == null) return false
        binding.viewPager.setCurrentItem(position, false)
        pendingTabId = null
        return true
    }

    private fun activeReaderTab(): ReaderTabFragment? =
        supportFragmentManager.fragments.filterIsInstance<ReaderTabFragment>().firstOrNull { it.isResumed }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                sidebarSwipeTab = activeReaderTab()
                sidebarSwipeDirection = sidebarSwipeTab?.sidebarSwipeDirection(event.rawX, event.rawY) ?: 0
                sidebarSwipeClosing = sidebarSwipeTab?.isPageSidebarOpen == true
                sidebarSwipeStartX = event.rawX
                sidebarSwipeStartY = event.rawY
                sidebarSwipeClaimed = false
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                if (sidebarSwipeDirection != 0) {
                    val slop = ViewConfiguration.get(this).scaledTouchSlop
                    val travel = (event.rawX - sidebarSwipeStartX) * sidebarSwipeDirection
                    val verticalTravel = abs(event.rawY - sidebarSwipeStartY)
                    val swipeDistance = (if (sidebarSwipeClosing) 24 else 40) * resources.displayMetrics.density
                    val horizontalDrag = travel > verticalTravel
                    val isUp = event.actionMasked == MotionEvent.ACTION_UP
                    val claimDistance = if (isUp) swipeDistance else slop.toFloat()
                    if (!sidebarSwipeClaimed) {
                        // Keep watching for a close swipe after initial vertical drift in the reader.
                        if (travel < -slop || (!sidebarSwipeClosing && verticalTravel > slop && !horizontalDrag)) {
                            sidebarSwipeDirection = 0
                        } else if (horizontalDrag && travel >= claimDistance) {
                            // Cancel the image or pager before it handles the rest of the swipe.
                            val cancel = MotionEvent.obtain(event)
                            cancel.action = MotionEvent.ACTION_CANCEL
                            super.dispatchTouchEvent(cancel)
                            cancel.recycle()
                            sidebarSwipeClaimed = true
                        }
                    }
                    if (isUp && sidebarSwipeClaimed && travel >= swipeDistance &&
                        sidebarSwipeTab === activeReaderTab()
                    ) {
                        sidebarSwipeTab?.togglePageSidebar()
                    }
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> sidebarSwipeDirection = 0
        }
        val handled = sidebarSwipeClaimed || super.dispatchTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
            sidebarSwipeTab = null
            sidebarSwipeDirection = 0
            sidebarSwipeClaimed = false
        }
        return handled
    }

    override fun onBackPressed() {
        if (activeReaderTab()?.dismissPageSidebar() != true) closeReader()
    }

    override fun openTabSwitcher() {
        val activeTabId = tabFragmentAdapter.tabIdAt(binding.viewPager.currentItem) ?: return
        val intent = Intent(this, RecentTabsActivity::class.java)
            .putExtra(RecentTabsActivity.ACTIVE_TAB_ID, activeTabId)
        startActivityForResult(intent, RECENT_TAB_REQUEST_CODE)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun quitActivity() = closeReader()

    internal fun onReaderPageReady(page: SubsamplingScaleImageView) {
        val name = page.transitionName ?: return
        readyPages.entries.removeAll { it.value.get()?.transitionName != it.key }
        readyPages[name] = WeakReference(page)
        if (name == openingPageName) {
            page.doOnPreDraw {
                if (page.transitionName == openingPageName) revealReader(animate = true)
            }
        }
    }

    internal fun onReaderPageError(page: SubsamplingScaleImageView) {
        if (page.transitionName == openingPageName) revealReader(animate = false)
    }

    private fun revealReader(animate: Boolean) {
        if (openingPageName == null) return
        openingPageName = null
        binding.root.removeCallbacks(showLoadingReader)
        binding.root.alpha = 1f
        if (animate && !isFinishing && !appLock.isLocked) {
            binding.root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.reader_open))
        }
    }

    override fun onDestroy() {
        binding.root.removeCallbacks(showLoadingReader)
        binding.root.clearAnimation()
        returnAnimation?.cancel()
        super.onDestroy()
    }

    private fun closeReader() {
        if (returningSharedPage || isFinishing) return
        returnLastReadPageNumber()
        val position = viewModel.lastReadImagePosition
        val source = transitionSource?.get()
        val currentDirectory = intent.getStringExtra(DoujinImagesAdapter.DIRECTORY_PATH)
        val originalTab = currentDirectory?.let { tabFragmentAdapter.getTab(it) }
        val onOriginalTab = originalTab?.id?.let { tabFragmentAdapter.getTabPosition(it) } ==
            binding.viewPager.currentItem
        if (appLock.isLocked || !onOriginalTab || source == null) {
            finishWithoutWindowAnimation()
            return
        }

        returningSharedPage = true
        source(position) { thumbnail ->
            if (isFinishing || isDestroyed) return@source
            val name = thumbnail?.transitionName
            val page = readyPages[name]?.get()
            if (!appLock.isLocked && thumbnail != null && page != null &&
                page.transitionName == name && page.isAttachedToWindow &&
                page.alpha > 0f && page.getGlobalVisibleRect(Rect())
            ) {
                returnAnimation = ReaderPageReturnAnimation.start(
                    page, binding.root, thumbnail, ::finishWithoutWindowAnimation
                )
            }
            if (returnAnimation == null) finishWithoutWindowAnimation()
        }
    }

    @Suppress("DEPRECATION")
    private fun finishWithoutWindowAnimation() {
        finish()
        if (Build.VERSION.SDK_INT < 34) overridePendingTransition(0, 0)
    }

    override fun setUserInputEnabled(isEnabled: Boolean) {
        binding.viewPager.isUserInputEnabled = isEnabled
    }

    override fun savePageNumber(pageNumber: Int) {
        viewModel.lastReadImagePosition = pageNumber
    }

    private fun returnLastReadPageNumber() {
        val positionInImageGrid =
            intent.getIntExtra(DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER, 0)

        val positionInReader = viewModel.lastReadImagePosition

        // Sends back the position of image currently being viewed if it's different with
        // the position of opened image in previous screen.
        if (positionInImageGrid != positionInReader) {
            val intent = Intent()
            intent.putExtra(DoujinImagesAdapter.POSITION_AFTER_EXITING_READER, positionInReader)
            setResult(Activity.RESULT_OK, intent)

        } else {
            setResult(Activity.RESULT_CANCELED)
        }
    }

    companion object {
        internal const val OPENING_PAGE = "reader_opening_page"
        private const val OPENING_PAGE_TIMEOUT_MS = 2_500L
        private var pendingSource: WeakReference<ReturnTarget>? = null

        internal fun setTransitionSource(source: ReturnTarget) {
            pendingSource = WeakReference(source)
        }

        internal fun pageTransitionName(file: File): String = "reader-page:" + file.absolutePath

        private const val SELECTED_TAB_ID = "selected_tab_id"
        const val RECENT_TAB_REQUEST_CODE = 101
    }
}
