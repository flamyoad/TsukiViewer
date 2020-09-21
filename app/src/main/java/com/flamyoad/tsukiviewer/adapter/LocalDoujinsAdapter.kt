package com.flamyoad.tsukiviewer.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.DoujinSortingMode
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity
import com.flamyoad.tsukiviewer.ui.home.local.TransitionAnimationListener
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import java.util.*


class LocalDoujinsAdapter(private val listener: TransitionAnimationListener) :
    RecyclerView.Adapter<LocalDoujinsAdapter.DoujinViewHolder>(),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    companion object {
        const val DOUJIN_FILE_PATH = "LocalDoujinsAdapter.DOUJIN_FILE_PATH"
        const val DOUJIN_NAME = "LocalDoujinsAdapter.DOUJIN_NAME"
        const val TRANSITION_NAME = "LocalDoujinsAdapter.TRANSITION_NAME"
    }

    private var doujinList: List<Doujin> = emptyList()

    private var currentSort = DoujinSortingMode.NONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoujinViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.doujin_list_item, parent, false)
        val holder = DoujinViewHolder(layout)

        layout.setOnClickListener {
            val context = parent.context
            val adapterPosition = holder.adapterPosition

            // Sometiems return -1 after we include new directory and the recyclerview gets invalidated
            if (adapterPosition == RecyclerView.NO_POSITION) {
                Log.d("adapter", "Adapter Position is -1")
                return@setOnClickListener
            }

            val doujin = doujinList[adapterPosition]

            val intent = Intent(context, DoujinDetailsActivity::class.java)
            intent.putExtra(DOUJIN_FILE_PATH, doujin.path.toString())
            intent.putExtra(DOUJIN_NAME, doujin.title)

            context.startActivity(intent)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return doujinList.size
    }

    override fun onBindViewHolder(holder: DoujinViewHolder, position: Int) {
        holder.bind(doujinList[holder.adapterPosition])
    }

    fun setList(list: List<Doujin>) {
        doujinList = list
        notifyDataSetChanged()
    }

    //    java.lang.NullPointerException: Attempt to invoke virtual method 'java.io.File com.flamyoad.tsukiviewer.model.Doujin.getPath()' on a null object reference
//    at com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter.getItemId(LocalDoujinsAdapter.kt:82)
    override fun getItemId(position: Int): Long {
        val pathName = doujinList[position].path.toString()
        return pathName.hashCode().toLong()
    }

    fun sortByName() {
        if (currentSort == DoujinSortingMode.TITLE_ASCENDING) {
            doujinList = doujinList.sortedByDescending {
                it.title.toLowerCase(Locale.ROOT)
            }
            currentSort = DoujinSortingMode.TITLE_DESCENDING

        } else {
            doujinList = doujinList.sortedBy {
                it.title.toLowerCase(Locale.ROOT)
            }
            currentSort = DoujinSortingMode.TITLE_ASCENDING
        }
        notifyDataSetChanged()
    }


    fun sortByDate() {
        if (currentSort == DoujinSortingMode.DATE_ASCENDING) {
            doujinList = doujinList.sortedByDescending {
                it.lastModified
            }
            currentSort = DoujinSortingMode.DATE_DESCENDING

        } else {
            doujinList = doujinList.sortedBy {
                it.lastModified
            }
            currentSort = DoujinSortingMode.DATE_ASCENDING
        }
        notifyDataSetChanged()
    }

    fun sortByPath() {
        if (currentSort == DoujinSortingMode.PATH_ASCENDING) {
            doujinList = doujinList.sortedByDescending {
                it.path.toString().toLowerCase(Locale.ROOT)
            }
            currentSort = DoujinSortingMode.PATH_DESCENDING

        } else {
            doujinList = doujinList.sortedBy {
                it.path.toString().toLowerCase(Locale.ROOT)
            }
            currentSort = DoujinSortingMode.PATH_ASCENDING
        }

        notifyDataSetChanged()
    }

    inner class DoujinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImg: ImageView = itemView.findViewById(R.id.imgCover)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitleEng)
        private val txtPageNumber: TextView = itemView.findViewById(R.id.txtPageNumber)

        fun bind(doujin: Doujin) {
            Glide.with(itemView.context)
                .load(doujin.pic)
                .transition(withCrossFade())
//                .thumbnail(0.25f)
                .sizeMultiplier(0.75f)
                .into(coverImg)

            txtTitle.text = doujin.title
            txtPageNumber.text = doujin.numberOfItems.toString()
        }
    }

    override fun onChange(position: Int): CharSequence {
        val doujin = doujinList[position]

        return when (currentSort) {
            DoujinSortingMode.TITLE_ASCENDING -> {
                doujin.title
            }

            DoujinSortingMode.TITLE_DESCENDING -> {
                doujin.title
            }

            DoujinSortingMode.DATE_ASCENDING -> {
                val datetime = Date(doujin.lastModified)
                return datetime.toString()
            }

            DoujinSortingMode.DATE_DESCENDING -> {
                val datetime = Date(doujin.lastModified)
                return datetime.toString()
            }

            DoujinSortingMode.PATH_ASCENDING -> {
                doujin.path.absolutePath
            }

            DoujinSortingMode.PATH_DESCENDING -> {
                doujin.path.absolutePath
            }

            else -> {
                doujin.title
            }
        }
    }
}