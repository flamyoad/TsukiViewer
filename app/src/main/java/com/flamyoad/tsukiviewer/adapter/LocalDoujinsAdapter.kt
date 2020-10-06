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
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller


class LocalDoujinsAdapter(private val listener: ActionModeListener<Doujin>) :
    RecyclerView.Adapter<LocalDoujinsAdapter.DoujinViewHolder>(),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    companion object {
        const val DOUJIN_FILE_PATH = "LocalDoujinsAdapter.DOUJIN_FILE_PATH"
        const val DOUJIN_NAME = "LocalDoujinsAdapter.DOUJIN_NAME"
    }

    private var doujinList: List<Doujin> = emptyList()

    var actionModeEnabled: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoujinViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.doujin_list_item, parent, false)
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

    //java.lang.NullPointerException: Attempt to invoke virtual method 'java.io.File com.flamyoad.tsukiviewer.model.Doujin.getPath()' on a null object reference
//    at com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter.getItemId(LocalDoujinsAdapter.kt:82)
    override fun getItemId(position: Int): Long {
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
        val doujin = doujinList[position]
        return doujin.title
    }
}