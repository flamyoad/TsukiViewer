package com.flamyoad.tsukiviewer.ui.doujinpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.flamyoad.tsukiviewer.core.repository.RelatedManga
import com.flamyoad.tsukiviewer.databinding.ItemRelatedMangaBinding

internal class RelatedMangaAdapter(
    private val images: RequestManager,
    private val open: (RelatedManga) -> Unit
) : ListAdapter<RelatedManga, RelatedMangaAdapter.Holder>(object : DiffUtil.ItemCallback<RelatedManga>() {
    override fun areItemsTheSame(oldItem: RelatedManga, newItem: RelatedManga) =
        oldItem.directory == newItem.directory

    override fun areContentsTheSame(oldItem: RelatedManga, newItem: RelatedManga) = oldItem == newItem
}) {
    class Holder(val binding: ItemRelatedMangaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemRelatedMangaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val match = getItem(position)
        with(holder.binding) {
            title.text = match.title
            reason.setText(match.reason.label)
            images.load(match.cover).into(cover)
            root.setOnClickListener { open(match) }
        }
    }

    override fun onViewRecycled(holder: Holder) {
        images.clear(holder.binding.cover)
        holder.binding.root.setOnClickListener(null)
    }
}
