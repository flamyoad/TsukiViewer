package com.flamyoad.tsukiviewer.utils

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter

class DoujinItemDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<String>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
//        val view = recyclerView.findChildViewUnder(event.x, event.y)
//        if (view != null) {
//            return (recyclerView.getChildViewHolder(view) as LocalDoujinsAdapter.DoujinViewHolder)
//                .getItemDetails()
//        }
        return null
    }
}