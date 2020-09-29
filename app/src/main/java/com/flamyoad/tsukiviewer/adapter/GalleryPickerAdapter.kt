package com.flamyoad.tsukiviewer.adapter

import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R

class GalleryPickerAdapter(val onGalleryPick: (String) -> Unit)
    : RecyclerView.Adapter<GalleryPickerAdapter.GalleryViewHolder>() {

    private var list: List<ResolveInfo> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_picker_item, parent, false)

        val holder = GalleryViewHolder(layout)

        layout.setOnClickListener {
            val resolveInfo = list[holder.adapterPosition]
            val pkgName = resolveInfo.activityInfo.packageName

            onGalleryPick(pkgName)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setList(list: List<ResolveInfo>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        private val txtAppName: TextView = itemView. findViewById (R.id.txtAppName)
        private val txtPkgName: TextView = itemView.findViewById(R.id.txtPackageName)

        fun bind(info: ResolveInfo) {
            val context = itemView.context
            val pm = context.packageManager

            val appIcon = info.loadIcon(pm)
            val appName = info.loadLabel(pm)
            val pkgName = info.activityInfo.packageName

            Glide.with(context)
                .load(appIcon)
                .into(thumbnail)

            txtAppName.text = appName
            txtPkgName.text = pkgName
        }
    }

}