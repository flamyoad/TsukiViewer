package com.flamyoad.tsukiviewer.ui.doujinpage

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinImagesAdapter
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.utils.CustomLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_grid_images.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentListImages : Fragment() {

    private val viewmodel by activityViewModels<DoujinViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_images, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listImages.setHasFixedSize(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val dirPath = requireActivity()
            .intent
            .getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)

        val adapter = DoujinImagesAdapter(DoujinImagesAdapter.ItemType.List, dirPath)

        val linearLayoutManager = LinearLayoutManager(context)

        listImages.adapter = adapter
        listImages.layoutManager = linearLayoutManager

        viewmodel.imageList.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
    }

    // SET RECYCLERVIEW HEIGHT TO SCREEN HEIGHT TO PREVENT GLIDE OOM
    private fun setRecyclerViewToFixedHeight() {
        val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels

        val layoutParams = listImages.layoutParams
        layoutParams.apply {
            height = screenHeight
            width = screenWidth
        }

        listImages.layoutParams = layoutParams
        listImages.setHasFixedSize(true)
    }

    companion object {
        @JvmStatic
        fun newInstance(): FragmentListImages {
            return FragmentListImages()
        }
    }
}
