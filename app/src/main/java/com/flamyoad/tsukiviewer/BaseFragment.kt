package com.flamyoad.tsukiviewer

import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    // Returns the title of this fragment to be shown in toolbar
    abstract fun getTitle(): String
}