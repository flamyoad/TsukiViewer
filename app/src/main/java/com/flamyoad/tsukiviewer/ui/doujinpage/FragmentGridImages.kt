package com.flamyoad.tsukiviewer.ui.doujinpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter.ItemType
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.utils.GridSpacingItemDecoration
import com.flamyoad.tsukiviewer.utils.ItemOffsetDecoration
import kotlinx.android.synthetic.main.fragment_grid_images.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class FragmentGridImages : Fragment() {

    private val GRID_ITEM_COUNT = 3
    private val SCALED_ITEM_COUNT = 2
    private val ROW_ITEM_COUNT = 1

    private val viewmodel by activityViewModels<DoujinViewModel>()

    private lateinit var adapter: DoujinImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_images, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val dirPath = requireActivity()
            .intent
            .getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)

        viewmodel.imageList().observe(viewLifecycleOwner, Observer {
             // No action here. However, LiveData needs to be observed for changes to be notified to it.
        })

        setListToRow(dirPath)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val dirPath = requireActivity()
            .intent
            .getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)

        when (item.itemId) {
            R.id.action_switch_to_grid -> setListToGrid(dirPath)
            R.id.action_switch_to_scaled -> setListToScaled(dirPath)
            R.id.action_switch_to_row -> setListToRow(dirPath)
        }

        return true
    }

    private fun setListToGrid(dirPath: String) {
        setupRecyclerview(dirPath, ItemType.Grid, GRID_ITEM_COUNT)
    }

    private fun setListToScaled(dirPath: String) {
        setupRecyclerview(dirPath, ItemType.Scaled, SCALED_ITEM_COUNT)
    }

    private fun setListToRow(dirPath: String) {
        setupRecyclerview(dirPath, ItemType.Row, ROW_ITEM_COUNT)
    }

    private fun setupRecyclerview(dirPath: String, type: DoujinImagesAdapter.ItemType, spanCount: Int) {
        adapter = DoujinImagesAdapter(type, dirPath)

        val gridLayoutManager = GridLayoutManager(context, spanCount)

        listImages.adapter = adapter
        listImages.layoutManager = gridLayoutManager

        // Clears previous item decoration added.
        // Otherwise, the decors stack on top of other. 1dp will become 2dp, 2 dp will become 3dp and so on...
        while (listImages.itemDecorationCount > 0) {
            listImages.removeItemDecorationAt(0)
        }

        val itemDecoration = GridSpacingItemDecoration(spanCount, 4, includeEdge = false)

        listImages.addItemDecoration(itemDecoration)

        val newList = viewmodel.imageList().value ?: emptyList()
        adapter.setList(newList)
    }

    companion object {
        @JvmStatic
        fun newInstance(): FragmentGridImages {
            return FragmentGridImages()
        }
    }
}
