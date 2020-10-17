package com.flamyoad.tsukiviewer.ui.reader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import com.flamyoad.tsukiviewer.R

class SwipeReaderFragment : Fragment() {
    private val viewModel: ReaderViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe_reader, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SwipeReaderFragment()
    }
}
