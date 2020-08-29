package com.flamyoad.tsukiviewer.ui.doujinpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.utils.GridSpacingItemDecoration
import com.flamyoad.tsukiviewer.utils.ItemOffsetDecoration
import kotlinx.android.synthetic.main.fragment_grid_images.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class FragmentGridImages : Fragment() {

    private val viewmodel by activityViewModels<DoujinViewModel>()

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

        val adapter = DoujinImagesAdapter(DoujinImagesAdapter.ItemType.Grid, dirPath)

        val gridLayoutManager = GridLayoutManager(context, 3)

        listImages.adapter = adapter
        listImages.layoutManager = gridLayoutManager

        val itemDecoration = GridSpacingItemDecoration(3, 4, includeEdge = false)

        listImages.addItemDecoration(itemDecoration)

        viewmodel.imageList().observe(viewLifecycleOwner, Observer {
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
