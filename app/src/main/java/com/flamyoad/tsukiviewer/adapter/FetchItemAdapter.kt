package com.flamyoad.tsukiviewer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import java.io.File

class FetchItemAdapter: RecyclerView.Adapter<FetchItemAdapter.FetchViewHolder>() {

    private var itemList = emptyList<File>()

    var count = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FetchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fetcher_list_item, parent, false)

        val holder = FetchViewHolder(view)

        Log.d("holder", "Created viewholder: $count")
        count++

        return holder
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FetchViewHolder, position: Int) {
        holder.bindTo(itemList[position])
    }

    fun setList(list: List<File>) {
        itemList = list
        notifyDataSetChanged()
    }

    inner class FetchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus)
        private val txtFolderName: TextView = itemView.findViewById(R.id.txtFolderName)
        private val txtPath: TextView = itemView.findViewById(R.id.txtPath)

        fun bindTo(file: File) {
            txtFolderName.text = file.name
            txtPath.text = file.absolutePath
        }
    }
}