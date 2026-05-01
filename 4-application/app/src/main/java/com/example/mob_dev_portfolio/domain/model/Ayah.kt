package com.example.mob_dev_portfolio.domain.model

/** A single ayah. `textTranslation` is null until the translation is loaded. */
data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val textTranslation: String?,
)
