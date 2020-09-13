package com.flamyoad.tsukiviewer.ui.home.online

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flamyoad.tsukiviewer.BaseFragment

import com.flamyoad.tsukiviewer.R

/**
 * A simple [Fragment] subclass.
 */
class OnlineDoujinFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_online_doujin, container, false)
    }

    override fun getTitle(): String {
        return APPBAR_TITLE
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            OnlineDoujinFragment()

        const val APPBAR_TITLE = "Online"
    }
}
