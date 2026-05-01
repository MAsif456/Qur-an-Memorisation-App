package com.example.mob_dev_portfolio.ui.recitation

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import androidx.core.content.ContextCompat
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.domain.model.Mistake
import com.example.mob_dev_portfolio.domain.model.MistakeType
import com.example.mob_dev_portfolio.domain.recitation.ArabicNormaliser

/** Builds a coloured-background span over the expected ayah using [Mistake.expectedIndex]. */
object MistakeHighlighter {

    fun render(context: Context, expected: String, mistakes: List<Mistake>): CharSequence {
        val tokens = expected.split(WHITESPACE_RE).filter { it.isNotBlank() }
        val mistakeByIndex = mistakes.associateBy { it.expectedIndex }
        val builder = SpannableStringBuilder()
        for ((index, token) in tokens.withIndex()) {
            if (index > 0) builder.append(' ')
            val start = builder.length
            builder.append(token)
            val end = builder.length
            val mistake = mistakeByIndex[index]
            if (mistake != null && mistake.type != MistakeType.EXTRA) {
                val colour = colourFor(context, mistake.type)
                builder.setSpan(BackgroundColorSpan(colour), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return builder
    }

    fun words(expected: String): List<String> = ArabicNormaliser.words(expected)

    private fun colourFor(context: Context, type: MistakeType): Int = ContextCompat.getColor(
        context,
        when (type) {
            MistakeType.MISSING -> R.color.mistake_missing
            MistakeType.SUBSTITUTED -> R.color.mistake_substituted
            MistakeType.EXTRA -> R.color.mistake_extra
        },
    )

    private val WHITESPACE_RE = Regex("\\s+")
}
