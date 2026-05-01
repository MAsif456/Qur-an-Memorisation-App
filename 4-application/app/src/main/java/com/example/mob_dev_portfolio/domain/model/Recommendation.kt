package com.example.mob_dev_portfolio.domain.model

/**
 * A recommended task for the Home screen.
 *
 * `score` is used only for ordering and is not displayed.
 */
data class Recommendation(
    val surahNumber: Int,
    val ayahNumber: Int,
    val reason: String,
    val score: Double,
)
