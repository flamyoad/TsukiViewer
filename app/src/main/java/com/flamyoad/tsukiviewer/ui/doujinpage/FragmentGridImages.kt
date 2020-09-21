package com.flamyoad.tsukiviewer.ui.doujinpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter.ItemType
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_grid_images.*

class FragmentGridImages : Fragment() {

    private val GRID_ITEM_SPAN = 3
    private val SCALED_ITEM_SPAN = 2
    private val ROW_ITEM_SPAN = 1

    private val viewModel by activityViewModels<DoujinViewModel>()

    private lateinit var adapter: DoujinImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        setupRecyclerview(dirPath, ItemType.Grid, GRID_ITEM_SPAN)
    }

    private fun setListToScaled(dirPath: String) {
        setupRecyclerview(dirPath, ItemType.Scaled, SCALED_ITEM_SPAN)
    }

    private fun setListToRow(dirPath: String) {
        setupRecyclerview(dirPath, ItemType.Row, ROW_ITEM_SPAN)
    }

    private fun setupRecyclerview(dirPath: String, type: ItemType, spanCount: Int) {
        adapter = DoujinImagesAdapter(type, dirPath)

        val gridLayoutManager = GridLayoutManager(context, spanCount)

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

    companion object {
        @JvmStatic
        fun newInstance(): FragmentGridImages {
            return FragmentGridImages()
        }
    }

}
