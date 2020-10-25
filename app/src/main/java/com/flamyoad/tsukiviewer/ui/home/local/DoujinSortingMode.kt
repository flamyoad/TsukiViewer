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
    SHORT_TITLE_ASC(R.id.btnShortTitleAsc),
    SHORT_TITLE_DESC(R.id.btnShortTitleDesc),
    NONE(-1);

    fun getLayoutId() = buttonId

    companion object {
        private val modeByView = values().associateBy(DoujinSortingMode::buttonId)

        fun fromLayout(layoutId: Int): DoujinSortingMode {
            return modeByView[layoutId] ?: NONE
        }

        fun getAllLayoutId(): List<Int> {
            return values()
                .filter { mode -> mode != NONE }
                .map { mode -> mode.buttonId }
        }
    }


}