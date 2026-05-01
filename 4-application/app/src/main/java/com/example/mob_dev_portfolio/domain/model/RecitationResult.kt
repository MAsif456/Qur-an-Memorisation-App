package com.example.mob_dev_portfolio.domain.model

import java.time.Instant

/**
 * Outcome of a single recitation attempt. `recogniser` is stored so we can
 * compare on-device vs. cloud results in any future analytics.
 */
data class RecitationResult(
    val surahNumber: Int,
    val ayahNumber: Int,
    val recordedAt: Instant,
    val transcript: String,
    val mistakes: List<Mistake>,
    val accuracy: AccuracyScore,
    val recogniser: String,
)
