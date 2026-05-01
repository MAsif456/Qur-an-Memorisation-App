package com.example.mob_dev_portfolio.ui.surahs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.domain.model.Surah

/** Surah list adapter; row taps go to the fragment. */
class SurahAdapter(
    private val onClick: (Surah) -> Unit,
) : ListAdapter<Surah, SurahAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_surah, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View, private val onClick: (Surah) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val number = itemView.findViewById<TextView>(R.id.surah_number)
        private val nameTranslit = itemView.findViewById<TextView>(R.id.surah_name_translit)
        private val nameArabic = itemView.findViewById<TextView>(R.id.surah_name_arabic)
        private val meta = itemView.findViewById<TextView>(R.id.surah_meta)

        fun bind(item: Surah) {
            number.text = item.number.toString()
            nameTranslit.text = item.nameTransliterated
            nameArabic.text = item.nameArabic
            meta.text = itemView.context.getString(
                R.string.surahs_meta_format,
                item.nameTranslation,
                item.ayahCount,
            )
            itemView.setOnClickListener { onClick(item) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Surah>() {
            override fun areItemsTheSame(old: Surah, new: Surah): Boolean = old.number == new.number
            override fun areContentsTheSame(old: Surah, new: Surah): Boolean = old == new
        }
    }
}
