package com.example.mob_dev_portfolio.domain.model

/**
 * A Surah (chapter) of the Quran.
 *
 * `revelationPlace` is kept as a free-form string ("makkah" / "madinah") so a
 * new value from the API doesn't break parsing.
 */
data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameTransliterated: String,
    val nameTranslation: String,
    val ayahCount: Int,
    val revelationPlace: String,
)
