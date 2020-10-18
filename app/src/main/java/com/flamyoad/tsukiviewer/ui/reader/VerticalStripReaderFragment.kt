package com.flamyoad.tsukiviewer.ui.reader

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.ReaderImageAdapter
import kotlinx.android.synthetic.main.fragment_vertical_strip_reader.*

class VerticalStripReaderFragment : Fragment() {
    private val viewModel: ReaderViewModel by activityViewModels()

    private var listener: ReaderListener? = null

    private val imageAdapter = ReaderImageAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vertical_strip_reader, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ReaderListener
        } catch (ignored: ClassCastException) { }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initReader()
        setupPageIndicator()

        viewModel.bottomThumbnailSelectedItem().observe(viewLifecycleOwner, Observer {
            if (!this::linearLayoutManager.isInitialized) return@Observer
            if (it == -1) return@Observer

            linearLayoutManager.scrollToPosition(it)

            viewModel.resetBottomThumbnailState()
        })
    }

    private fun initReader() {
        val currentDir = arguments?.getString(CURRENT_DIR) ?: ""
        val positionFromImageGrid = arguments?.getInt(POSITION_BEFORE_OPENING_READER, 0) ?: 0

        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listImages.adapter = imageAdapter
        listImages.layoutManager = linearLayoutManager
        listImages.setHasFixedSize(true)

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            imageAdapter.setList(it)

            if (viewModel.currentPath.isBlank()) {
                linearLayoutManager.scrollToPosition(positionFromImageGrid)
                listener?.onPageChange(positionFromImageGrid)
            } else {
                linearLayoutManager.scrollToPosition(viewModel.currentImagePosition)
            }

            viewModel.currentPath = currentDir
        })
    }

    private fun setupPageIndicator() {
        val currentPage = viewModel.currentImagePosition
        listener?.onPageChange(currentPage)

        listImages.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) {
                    listener?.toggleBottomSheet(View.INVISIBLE)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastCompletelyVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val firstVisible = linearLayoutManager.findFirstVisibleItemPosition()

                if (lastCompletelyVisible != RecyclerView.NO_POSITION) {
                    listener?.onPageChange(lastCompletelyVisible)
                } else {
                    listener?.onPageChange(firstVisible)
                }
            }
        })
    }

    companion object {
        const val CURRENT_DIR = "current_dir"
        const val POSITION_BEFORE_OPENING_READER = "position_before_opening_reader"

        @JvmStatic
        fun newInstance(currentDir: String, positionBeforeOpenReader: Int) =
            VerticalStripReaderFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_DIR, currentDir)
                    putInt(POSITION_BEFORE_OPENING_READER, positionBeforeOpenReader)
                }
            }
    }
}
