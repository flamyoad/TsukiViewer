package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.model.RecentTab
import com.flamyoad.tsukiviewer.databinding.ReaderTabItemBinding
import kotlin.math.roundToInt

class RecentTabsAdapter(
    private val onTabClick: (RecentTab) -> Unit,
    private val onClose: (RecentTab) -> Unit
) : ListAdapter<RecentTab, RecentTabsAdapter.TabViewHolder>(TabDiffUtil()) {

    init {
        setHasStableIds(true)
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    var activeTabId: Long? = null
        set(value) {
            if (field == value) return
            val previous = field
            field = value
            currentList.forEachIndexed { index, tab ->
                if (tab.id == previous || tab.id == value) notifyItemChanged(index)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder =
        TabViewHolder(ReaderTabItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).id ?: RecyclerView.NO_ID

    override fun onViewRecycled(holder: TabViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    inner class TabViewHolder(private val binding: ReaderTabItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tab: RecentTab) {
            val context = binding.root.context
            val active = tab.id == activeTabId
            binding.root.strokeColor = ContextCompat.getColor(context, R.color.read_mode_button_active)
            binding.root.strokeWidth = if (active) {
                (2 * context.resources.displayMetrics.density).roundToInt()
            } else {
                0
            }
            binding.root.contentDescription = if (active) {
                context.getString(R.string.reader_tabs_active, tab.title)
            } else {
                tab.title
            }
            binding.txtDoujinTitle.text = tab.title
            binding.closeTab.contentDescription = context.getString(R.string.reader_tabs_close, tab.title)
            binding.root.setOnClickListener { onTabClick(tab) }
            binding.closeTab.setOnClickListener { onClose(tab) }

            Glide.with(binding.imgScreenshot)
                .load(tab.thumbnail)
                .centerCrop()
                .into(binding.imgScreenshot)
        }

        fun recycle() {
            Glide.with(binding.imgScreenshot).clear(binding.imgScreenshot)
        }
    }
}

private class TabDiffUtil : DiffUtil.ItemCallback<RecentTab>() {
    override fun areItemsTheSame(oldItem: RecentTab, newItem: RecentTab): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: RecentTab, newItem: RecentTab): Boolean =
        oldItem == newItem
}
