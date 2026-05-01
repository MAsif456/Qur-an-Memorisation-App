package com.example.mob_dev_portfolio.ui.tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.R

/** Two-line row shared by every Tracker tab. */
data class TrackerRow(val key: String, val title: String, val subtitle: String)

class TrackerRowAdapter : ListAdapter<TrackerRow, TrackerRowAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tracker_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.tracker_row_title)
        private val subtitle = itemView.findViewById<TextView>(R.id.tracker_row_subtitle)

        fun bind(row: TrackerRow) {
            title.text = row.title
            subtitle.text = row.subtitle
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<TrackerRow>() {
            override fun areItemsTheSame(old: TrackerRow, new: TrackerRow): Boolean = old.key == new.key
            override fun areContentsTheSame(old: TrackerRow, new: TrackerRow): Boolean = old == new
        }
    }
}
