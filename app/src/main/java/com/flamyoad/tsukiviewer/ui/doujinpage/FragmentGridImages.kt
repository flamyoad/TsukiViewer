package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.view.OneShotPreDrawListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.tsukiviewer.MyAppPreference
import com.flamyoad.tsukiviewer.MyAppPreference.XmlGrid
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter.ItemType
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.databinding.FragmentGridImagesBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.utils.ui.GridPinchGesture
import javax.inject.Inject

const val GRID_ITEM_SPAN_PORTRAIT = 3
const val GRID_ITEM_SPAN_LANDSCAPE = 5

const val SCALED_ITEM_SPAN_PORTRAIT = 2
const val SCALED_ITEM_SPAN_LANDSCAPE = 3

const val ROW_ITEM_SPAN = 1

const val IMAGE_POSITION_REQUEST_CODE = 100

const val DIALOG_VIEW_STYLE = "dialog_view_style"

class FragmentGridImages : Fragment() {
    private var _binding: FragmentGridImagesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: DoujinViewModel by activityViewModels { viewModelFactory }

    private var adapter: DoujinImagesAdapter? = null

    private lateinit var gridLayoutManager: GridLayoutManager

    private var readerPosition: Int = -1

    private val returnTarget: (Int, (ImageView?) -> Unit) -> Unit = ::prepareReturnThumbnail

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGridImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        binding.listImages.gridPinch.reset()
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    override fun onResume() {
        super.onResume()
        syncGridPositionWithReader() // Called after onActivityCreated()

        // First initialization
        if (adapter == null) {
            val dirPath = requireActivity()
                .intent
                .getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH) ?: ""

            viewModel.gridViewStyle().observe(viewLifecycleOwner, Observer { style ->
                when (style) {
                    GridViewStyle.Grid -> setListToGrid(dirPath)
                    GridViewStyle.Scaled -> setListToScaled(dirPath)
                    GridViewStyle.Row -> setListToRow(dirPath)
                    GridViewStyle.List -> setListToList(dirPath)
                    else -> {}
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_doujin_detail_imagegrid, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_normal_grid -> {
                val fragment = DialogViewStyle.newInstance()
                fragment.show(childFragmentManager, DIALOG_VIEW_STYLE)
            }
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.imageList().observe(viewLifecycleOwner, Observer { adapter?.setList(it) })

        viewModel.directoryNoLongerExists().observe(viewLifecycleOwner, Observer { notExists ->
            if (notExists) {
                requireActivity().finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_POSITION_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> data?.let {
                    readerPosition =
                        it.getIntExtra(DoujinImagesAdapter.POSITION_AFTER_EXITING_READER, -1)
                }

                // Clears the old position in case the reader and grid have the same position
                // If we do not clear this, then the grid will jump back to previous location
                Activity.RESULT_CANCELED -> {
                    readerPosition = -1
                }
            }
        }
    }

    private fun setListToGrid(dirPath: String) {
        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> GRID_ITEM_SPAN_PORTRAIT
            Configuration.ORIENTATION_LANDSCAPE -> GRID_ITEM_SPAN_LANDSCAPE
            else -> GRID_ITEM_SPAN_PORTRAIT // Not worth crashing the app by throwing IllegalArgumentException
        }
        setupRecyclerview(dirPath, ItemType.Grid, spanCount)
    }

    private fun setListToScaled(dirPath: String) {
        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> SCALED_ITEM_SPAN_PORTRAIT
            Configuration.ORIENTATION_LANDSCAPE -> SCALED_ITEM_SPAN_LANDSCAPE
            else -> GRID_ITEM_SPAN_PORTRAIT // Not worth crashing the app by throwing IllegalArgumentException
        }
        setupRecyclerview(dirPath, ItemType.Scaled, spanCount)
    }

    private fun setListToRow(dirPath: String) {
        setupRecyclerview(dirPath, ItemType.Row, ROW_ITEM_SPAN)
    }

    fun setListToList(dirPath: String) {
        setupRecyclerview(dirPath, ItemType.List, ROW_ITEM_SPAN)
    }

    @Suppress("DEPRECATION")
    private fun setupRecyclerview(dirPath: String, type: ItemType, defaultSpan: Int) {
        val recycler = binding.listImages
        val currentPosition = (recycler.layoutManager as? GridLayoutManager)
            ?.findFirstVisibleItemPosition() ?: -1
        val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val maxSpan = GridPinchGesture.maxSpanCount(landscape)
        val canResize = type == ItemType.Grid || type == ItemType.Scaled
        val preference = MyAppPreference.getInstance(requireContext())
        val span = if (canResize) {
            preference.getXmlGridSpan(XmlGrid.IMAGES, type, landscape, defaultSpan)
                .coerceIn(GridPinchGesture.MIN_SPAN_COUNT, maxSpan)
        } else {
            defaultSpan
        }

        adapter = DoujinImagesAdapter(type, dirPath) { intent ->
            ReaderActivity.setTransitionSource(returnTarget)
            val noAnimation = R.anim.reader_no_window_animation
            val options = ActivityOptions.makeCustomAnimation(requireContext(), noAnimation, noAnimation)
            startActivityForResult(intent, IMAGE_POSITION_REQUEST_CODE, options.toBundle())
        }

        adapter?.setHasStableIds(true)
        viewModel.imageList().value?.let { adapter?.setList(it) }

        gridLayoutManager = GridLayoutManager(context, span)
        recycler.adapter = adapter
        recycler.layoutManager = gridLayoutManager
        recycler.gridPinch.configure(maxSpan, spacing = 4, includeEdge = false, enabled = canResize) {
            preference.setXmlGridSpan(XmlGrid.IMAGES, type, landscape, it)
        }
        if (currentPosition >= 0) gridLayoutManager.scrollToPosition(currentPosition)
    }

    private fun prepareReturnThumbnail(position: Int, onReady: (ImageView?) -> Unit) {
        val list = _binding?.listImages
        if (list == null || !list.isAttachedToWindow ||
            position !in 0 until (list.adapter?.itemCount ?: 0)
        ) {
            onReady(null)
            return
        }

        fun visibleThumbnail(): ImageView? {
            val image = list.findViewHolderForAdapterPosition(position)
                ?.itemView?.findViewById<ImageView>(R.id.imageView) ?: return null
            val bounds = Rect()
            return image.takeIf {
                it.drawable != null && it.getLocalVisibleRect(bounds)
            }
        }

        val currentThumbnail = visibleThumbnail()
        if (currentThumbnail != null) {
            onReady(currentThumbnail)
            return
        }

        // The overview remains behind the translucent reader during layout and image loading.
        gridLayoutManager.scrollToPositionWithOffset(position, 0)
        lateinit var preDraw: OneShotPreDrawListener
        val timeout = Runnable {
            preDraw.removeListener()
            onReady(null)
        }
        fun awaitThumbnail() {
            preDraw = OneShotPreDrawListener.add(list) {
                val image = visibleThumbnail()
                if (image != null) {
                    list.removeCallbacks(timeout)
                    onReady(image)
                } else {
                    awaitThumbnail()
                    list.postInvalidateOnAnimation()
                }
            }
        }
        awaitThumbnail()
        list.postDelayed(timeout, RETURN_THUMBNAIL_TIMEOUT_MS)
    }

    // Scrolls the list to the last seen item in the ViewPager in the ReaderActivity after user presses the back button
    private fun syncGridPositionWithReader() {
        if (binding.listImages.layoutManager == null) {
            return
        }

        // -1 means there is no need to readjust position in grid
        if (readerPosition == -1) {
            return
        }

        val firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition()

        // If the item viewed in reader is not visible in the grid
        // (For example, when a user started reading from Page 1 until Page 43)
        // Page 43 would be not visible in the grid when the user first clicked on it. So, we have to readjust the position
        if (readerPosition !in firstVisiblePosition..lastVisiblePosition) {
            gridLayoutManager.scrollToPosition(readerPosition)
        }

        // Clears the last position given
        readerPosition = -1
    }

    companion object {
        private const val RETURN_THUMBNAIL_TIMEOUT_MS = 700L
        @JvmStatic
        fun newInstance(): FragmentGridImages {
            return FragmentGridImages()
        }
    }

}
