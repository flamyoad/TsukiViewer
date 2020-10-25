package com.flamyoad.tsukiviewer.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity
import com.flamyoad.tsukiviewer.ui.settings.includedfolders.IncludedFolderActivity
import com.flamyoad.tsukiviewer.utils.EmptyViewHolder
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller


const val EMPTY_LIST_INDICATOR = 0
const val LIST_ITEM = 1

class LocalDoujinsAdapter(private val listener: ActionModeListener<Doujin>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    companion object {
        const val DOUJIN_FILE_PATH = "LocalDoujinsAdapter.DOUJIN_FILE_PATH"
        const val DOUJIN_NAME = "LocalDoujinsAdapter.DOUJIN_NAME"
    }

    private var doujinList: List<Doujin> = emptyList()

    var actionModeEnabled: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            EMPTY_LIST_INDICATOR -> {
                val layout = inflater.inflate(R.layout.doujin_list_empty_indicator, parent, false)
                val holder = EmptyViewHolder(layout)

                layout.setOnClickListener {
                    val context = parent.context
                    val intent = Intent(context, IncludedFolderActivity::class.java)

                    context.startActivity(intent)
                }

                return holder
            }

            LIST_ITEM -> {
                val layout = inflater.inflate(R.layout.doujin_list_item, parent, false)
                val holder = DoujinViewHolder(layout)

                layout.setOnClickListener {
                    val context = parent.context
                    val adapterPosition = holder.adapterPosition

                    // Sometiems return -1 after we include new directory and the recyclerview gets invalidated
                    if (adapterPosition == RecyclerView.NO_POSITION) {
                        return@setOnClickListener
                    }

                    val doujin = doujinList[adapterPosition]

                    when (actionModeEnabled) {
                        true -> {
                            listener.onMultiSelectionClick(doujin)
                        }
                        false -> {
                            val intent = Intent(context, DoujinDetailsActivity::class.java).apply {
                                putExtra(DOUJIN_FILE_PATH, doujin.path.toString())
                                putExtra(DOUJIN_NAME, doujin.title)
                            }
                            context.startActivity(intent)
                        }
                    }
                }

                layout.setOnLongClickListener {
                    val adapterPosition = holder.adapterPosition
                    if (adapterPosition == RecyclerView.NO_POSITION) {
                        return@setOnLongClickListener true
                    }

                    if (!actionModeEnabled) {
                        val zoomIn = AnimationUtils.loadAnimation(it.context, R.anim.doujin_img_zoom_in)
                        val zoomOut = AnimationUtils.loadAnimation(it.context, R.anim.doujin_img_zoom_out)

                        val coverImage: ImageView = it.findViewById(R.id.imgCover)

                        coverImage.startAnimation(zoomIn)
                        listener.startActionMode()
                        coverImage.startAnimation(zoomOut)
                    }

                    val item = doujinList[adapterPosition]

                    listener.onMultiSelectionClick(item)
                    return@setOnLongClickListener true
                }
                return holder
            }

            else -> throw IllegalArgumentException("View type doesn't exist")
        }
    }

    override fun getItemCount(): Int {
        if (doujinList.isEmpty()) {
            return 1
        } else {
            return doujinList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (doujinList.isEmpty()) {
            return EMPTY_LIST_INDICATOR
        } else {
            return LIST_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            LIST_ITEM -> (holder as DoujinViewHolder).bind(doujinList[holder.adapterPosition])
        }
    }

    fun setList(list: List<Doujin>) {
        doujinList = list
        notifyDataSetChanged()
    }

    //java.lang.NullPointerException: Attempt to invoke virtual method 'java.io.File com.flamyoad.tsukiviewer.model.Doujin.getPath()' on a null object reference
//    at com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter.getItemId(LocalDoujinsAdapter.kt:82)
    override fun getItemId(position: Int): Long {
        if (doujinList.isEmpty()) {
            return 0
        }

        val pathName = doujinList[position].path.toString()
        return pathName.hashCode().toLong()
    }

    inner class DoujinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImg: ImageView = itemView.findViewById(R.id.imgCover)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitleEng)
        private val txtPageNumber: TextView = itemView.findViewById(R.id.txtPageNumber)
        private val multiSelectIndicator: ImageView =
            itemView.findViewById(R.id.multiSelectIndicator)

        fun bind(doujin: Doujin) {
            Glide.with(itemView.context)
                .load(doujin.pic)
                .transition(withCrossFade())
                .sizeMultiplier(0.75f)
                .into(coverImg)

            txtTitle.text = doujin.title
            txtPageNumber.text = doujin.numberOfItems.toString()

            when (doujin.isSelected) {
                true -> setIconVisibility(View.VISIBLE)
                false -> setIconVisibility(View.GONE)
            }
        }

        private fun setIconVisibility(visibility: Int) {
            when (visibility) {
                View.VISIBLE -> multiSelectIndicator.visibility = visibility
                View.GONE -> multiSelectIndicator.visibility = visibility
                else -> return
            }
        }
    }

    override fun onChange(position: Int): CharSequence {
        if (doujinList.isEmpty()) {
            return ""
        }

        val doujin = doujinList[position]
        return doujin.title
    }
}