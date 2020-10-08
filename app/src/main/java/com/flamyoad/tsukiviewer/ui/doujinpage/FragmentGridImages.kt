package com.flamyoad.tsukiviewer.ui.doujinpage

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter.ItemType
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_grid_images.*

const val GRID_ITEM_SPAN_PORTRAIT = 3
const val GRID_ITEM_SPAN_LANDSCAPE = 5

const val SCALED_ITEM_SPAN_PORTRAIT = 2
const val SCALED_ITEM_SPAN_LANDSCAPE = 3

const val ROW_ITEM_SPAN = 1

const val IMAGE_POSITION_REQUEST_CODE = 100

class FragmentGridImages : Fragment() {
    private val viewModel by activityViewModels<DoujinViewModel>()

    private lateinit var adapter: DoujinImagesAdapter

    private lateinit var gridLayoutManager: GridLayoutManager

    private var readerPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        syncGridPositionWithReader() // Called after onActivityCreated()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grid_images, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val dirPath = requireActivity()
            .intent
            .getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)

        setListToScaled(dirPath)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val dirPath = requireActivity()
            .intent
            .getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)

        // Gets the current item position before replacing the adapter
        val currentPosition = gridLayoutManager.findFirstVisibleItemPosition()

        // Replaces the current adapter and layout manager with new ones
        when (item.itemId) {
            R.id.action_switch_to_grid -> {
                setListToGrid(dirPath)
                gridLayoutManager.scrollToPosition(currentPosition) // If we put this method outside of the block, it will be triggered on the parent menuItem click.
            }

            R.id.action_switch_to_scaled -> {
                setListToScaled(dirPath)
                gridLayoutManager.scrollToPosition(currentPosition)
            }

            R.id.action_switch_to_row -> {
                setListToRow(dirPath)
                gridLayoutManager.scrollToPosition(currentPosition)
            }
        }
        return true
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

    private fun setupRecyclerview(dirPath: String, type: ItemType, spanCount: Int) {
        adapter = DoujinImagesAdapter(type, dirPath) {
            startActivityForResult(it, IMAGE_POSITION_REQUEST_CODE)
        }

        adapter.setHasStableIds(true)

        gridLayoutManager = GridLayoutManager(context, spanCount)

        listImages.adapter = adapter
        listImages.layoutManager = gridLayoutManager
        /*
           Since this method is called each time the view type is changed,
           We have to clear the item decorations added previously.
           Otherwise, the decors stack on top of other. 1dp will become 2dp, 2 dp will become 3dp and so on...
         */
        while (listImages.itemDecorationCount > 0) {
            listImages.removeItemDecorationAt(0)
        }

        val itemDecoration = GridItemDecoration(spanCount, 4, includeEdge = false)

        listImages.addItemDecoration(itemDecoration)

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
    }

    // Scrolls the list to the last seen item in the ViewPager in the ReaderActivity after user presses the back button
    private fun syncGridPositionWithReader() {
        if (!this::gridLayoutManager.isInitialized) {
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
        @JvmStatic
        fun newInstance(): FragmentGridImages {
            return FragmentGridImages()
        }
    }

}
