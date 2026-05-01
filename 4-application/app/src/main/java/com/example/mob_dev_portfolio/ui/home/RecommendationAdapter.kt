package com.example.mob_dev_portfolio.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.domain.model.Recommendation
import com.google.android.material.card.MaterialCardView

/** Adapter for the Home recommendation list. */
class RecommendationAdapter(
    private val onClick: (Recommendation) -> Unit,
) : ListAdapter<Recommendation, RecommendationAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommendation, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onClick: (Recommendation) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {

        private val card = itemView as MaterialCardView
        private val title = itemView.findViewById<android.widget.TextView>(R.id.recommendation_title)
        private val reason = itemView.findViewById<android.widget.TextView>(R.id.recommendation_reason)

        fun bind(item: Recommendation) {
            val context = itemView.context
            title.text = context.getString(
                R.string.home_recommendation_title_format,
                item.surahNumber,
                item.ayahNumber,
            )
            reason.text = item.reason
            card.setOnClickListener { onClick(item) }
        }
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Recommendation>() {
            override fun areItemsTheSame(old: Recommendation, new: Recommendation): Boolean =
                old.surahNumber == new.surahNumber && old.ayahNumber == new.ayahNumber

            override fun areContentsTheSame(old: Recommendation, new: Recommendation): Boolean =
                old == new
        }
    }
}
