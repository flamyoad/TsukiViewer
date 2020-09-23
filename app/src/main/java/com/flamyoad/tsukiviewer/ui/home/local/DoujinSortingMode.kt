package com.flamyoad.tsukiviewer.ui.home.local

import com.flamyoad.tsukiviewer.R

enum class DoujinSortingMode(private val buttonId: Int) {

    TITLE_ASC(R.id.btnSortTitleAsc),
    TITLE_DESC(R.id.btnSortTitleDesc),
    DATE_ASC(R.id.btnSortDateAsc),
    DATE_DESC(R.id.btnSortDateDesc),
    NUM_ITEMS_ASC(R.id.btnPagesAsc),
    NUM_ITEMS_DESC(R.id.btnPagesDesc),
    PATH_ASC(R.id.btnPathAsc),
    PATH_DESC(R.id.btnPathDesc),
    NONE(-1);

    fun getLayoutId() = buttonId

    companion object {
        fun fromLayout(layoutId: Int): DoujinSortingMode {
            return when (layoutId) {
                R.id.btnSortTitleAsc -> TITLE_ASC
                R.id.btnSortTitleDesc -> TITLE_DESC
                R.id.btnSortDateAsc -> DATE_ASC
                R.id.btnSortDateDesc -> DATE_DESC
                R.id.btnPagesAsc -> NUM_ITEMS_ASC
                R.id.btnPagesDesc -> NUM_ITEMS_DESC
                R.id.btnPathAsc -> PATH_ASC
                R.id.btnPathDesc -> PATH_DESC
                else -> NONE
            }
        }
    }


}