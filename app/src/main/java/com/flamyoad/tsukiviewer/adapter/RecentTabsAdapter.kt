package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.RecentTab

class RecentTabsAdapter(
    private val onTabClick: (RecentTab) -> Unit,
    private val startDrag: (RecyclerView.ViewHolder) -> Unit,
    private val currentTabId: Long
) :
    ListAdapter<RecentTab, RecentTabsAdapter.TabViewHolder>(TabDiffUtil()) {

    private var oldX: Float = 0f
    private var oldY: Float = 0f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reader_tab_item, parent, false)

        val holder = TabViewHolder(layout)

        layout.setOnClickListener {
            val position = holder.bindingAdapterPosition
            onTabClick(getItem(position))
        }

        layout.setOnTouchListener { view, motionEvent ->
            val isCurrentTabId = getItemId(holder.bindingAdapterPosition) == currentTabId
            if (!isCurrentTabId) {
                return@setOnTouchListener false
            }

            // https://stackoverflow.com/questions/3148741/how-to-capture-finger-movement-direction-in-android-phone
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    oldX = motionEvent.x
                    oldY = motionEvent.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = motionEvent.x
                    val newY = motionEvent.y

                    val dx = oldX - newX
                    val dy = oldY - newY

                    if (Math.abs(dy) > Math.abs(dx)) { // Motion in Y direction.
                        startDrag(holder)
                    } else {
                        // Motion in X direction.
                    }
                }
            }
            return@setOnTouchListener false
        }

        return holder
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id ?: -1
    }

    fun getRecentTab(position: Int): RecentTab {
        return getItem(position)
    }

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgScreenshot: ImageView = itemView.findViewById(R.id.imgScreenshot)
        private val txtDoujinTitle: TextView = itemView.findViewById(R.id.txtDoujinTitle)

        fun bind(tabHistory: RecentTab) {
            Glide.with(itemView.context)
                .load(tabHistory.thumbnail)
                .transition(withCrossFade())
                .into(imgScreenshot)

            txtDoujinTitle.text = tabHistory.title
        }
    }
}

class TabDiffUtil : DiffUtil.ItemCallback<RecentTab>() {
    override fun areItemsTheSame(oldItem: RecentTab, newItem: RecentTab): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentTab, newItem: RecentTab): Boolean {
        return oldItem == newItem
    }
}