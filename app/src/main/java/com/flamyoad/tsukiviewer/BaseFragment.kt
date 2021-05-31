package com.flamyoad.tsukiviewer

import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    /**
     * Returns the title of this fragment to be shown in toolbar
     */
    abstract fun getTitle(): String

    /**
     * Instead of destroying and restoring ActionMode in screen rotation, we only destroy it when user goes to another
     * screen in Navigation Drawer
     */
    abstract fun destroyActionMode()
}