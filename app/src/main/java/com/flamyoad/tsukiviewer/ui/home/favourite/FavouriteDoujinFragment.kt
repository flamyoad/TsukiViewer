package com.flamyoad.tsukiviewer.ui.home.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import com.flamyoad.tsukiviewer.R

class FavouriteDoujinFragment : Fragment() {

    private val viewmodel: FavouriteDoujinViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_doujin, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavouriteDoujinFragment()
    }
}
