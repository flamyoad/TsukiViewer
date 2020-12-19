package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Source

class SourceSelectorAdapter(val checkBoxStates: HashMap<Source, Boolean>,
                            val onCheckBoxClick: (Source, Boolean) -> Unit)
    : RecyclerView.Adapter<SourceSelectorAdapter.SourceViewHolder>() {

    private val sourceList = Source.values()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_fetch_sources_item, parent, false)

        val holder = SourceViewHolder(layout)

        return holder
    }

    override fun getItemCount(): Int {
        return sourceList.size
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(sourceList[position])

        val isChecked = checkBoxStates.get(sourceList[position]) ?: true
        holder.setCheckbox(isChecked)

        checkBoxStates[sourceList[position]] = isChecked
    }

    fun restoreSelectedItems(sourceNames: Array<String>) {
        for (entry in checkBoxStates) {
            val key = entry.key

            if (key.readableName in sourceNames) {
                entry.setValue(true)
            } else {
                entry.setValue(false)
            }
        }
        notifyDataSetChanged()
    }

    inner class SourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rootLayout: ViewGroup = itemView.findViewById(R.id.rootLayout)
        private val imgLogo: ImageView = itemView.findViewById(R.id.imgLogo)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtApproximate: TextView = itemView.findViewById(R.id.txtApproximate)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(source: Source) {
            val context = itemView.context

            val drawable = ContextCompat.getDrawable(context, source.drawableId)
            Glide.with(context)
                .load(drawable)
                .into(imgLogo)

            txtTitle.text = source.readableName
            txtApproximate.text =
                context.getString(R.string.approximate_time_per_request, source.secondPerRequest)

            rootLayout.setOnClickListener {
                val newValue = !checkBox.isChecked
                checkBox.isChecked = newValue
            }

            checkBox.setOnCheckedChangeListener { compoundButton, newValue ->
                onCheckBoxClick(source, newValue)
            }
        }

        fun setCheckbox(isChecked: Boolean) {
            checkBox.isChecked = isChecked
        }
    }
}