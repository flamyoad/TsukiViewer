package com.flamyoad.tsukiviewer.ui.reader

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.flamyoad.tsukiviewer.core.model.ReaderSpreads
import java.io.File

internal class ImageFragmentStateAdapter(
    fm: FragmentManager,
    private val images: List<File>,
    private val spreads: ReaderSpreads
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): ImageFragment {
        val pages = spreads.pagesForItem(position)
        return ImageFragment.newInstance(
            images[pages.first],
            images[pages.last].takeIf { pages.first != pages.last },
            spreads.rightToLeft
        )
    }

    override fun getCount() = spreads.itemCount
}
