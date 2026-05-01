package com.example.mob_dev_portfolio.ui.surahs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.domain.model.Ayah
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.google.android.material.button.MaterialButton

/** Ayah plus its current status, so the row can flip the toggle label. */
data class AyahRow(val ayah: Ayah, val status: MemorisationStatus)

/** Ayah list adapter for [SurahDetailFragment]. */
class AyahAdapter(
    private val onMemoriseToggle: (AyahRow) -> Unit,
    private val onRecite: (AyahRow) -> Unit,
) : ListAdapter<AyahRow, AyahAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ayah, parent, false)
        return ViewHolder(view, onMemoriseToggle, onRecite)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onMemoriseToggle: (AyahRow) -> Unit,
        private val onRecite: (AyahRow) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {
        private val number = itemView.findViewById<TextView>(R.id.ayah_number)
        private val arabic = itemView.findViewById<TextView>(R.id.ayah_text_arabic)
        private val translation = itemView.findViewById<TextView>(R.id.ayah_text_translation)
        private val memoriseButton = itemView.findViewById<MaterialButton>(R.id.ayah_button_memorise)
        private val reciteButton = itemView.findViewById<MaterialButton>(R.id.ayah_button_recite)

        fun bind(row: AyahRow) {
            val context = itemView.context
            number.text = context.getString(R.string.ayah_number_format, row.ayah.ayahNumber)
            arabic.text = row.ayah.textArabic
            translation.text = row.ayah.textTranslation.orEmpty()
            translation.visibility = if (row.ayah.textTranslation.isNullOrBlank()) View.GONE else View.VISIBLE
            memoriseButton.text = if (row.status == MemorisationStatus.NEW) {
                context.getString(R.string.action_mark_memorised)
            } else {
                context.getString(R.string.action_unmark_memorised)
            }
            memoriseButton.setOnClickListener { onMemoriseToggle(row) }
            reciteButton.setOnClickListener { onRecite(row) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<AyahRow>() {
            override fun areItemsTheSame(old: AyahRow, new: AyahRow): Boolean =
                old.ayah.surahNumber == new.ayah.surahNumber &&
                    old.ayah.ayahNumber == new.ayah.ayahNumber

            override fun areContentsTheSame(old: AyahRow, new: AyahRow): Boolean = old == new
        }
    }
}
