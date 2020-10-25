package com.flamyoad.tsukiviewer.ui.doujinpage

import com.flamyoad.tsukiviewer.R

enum class GridViewStyle(private val layoutId: Int) {
    Grid(R.id.btnGrid),
    Scaled(R.id.btnScaled),
    Row(R.id.btnRow),
    List(R.id.btnList),

    None(-1);

    companion object {
        private val modeByView = values().associateBy(GridViewStyle::layoutId)

        fun fromLayout(layoutId: Int): GridViewStyle {
            return modeByView[layoutId] ?: None
        }
    }
}