package com.example.mob_dev_portfolio.domain.model

import java.time.Instant

/**
 * Memorisation state plus the SM-2 schedule for a single ayah.
 *
 * `lastAccuracy` is null until the user has recited the ayah at least once;
 * the recommender uses it to surface weak ayahs.
 */
data class AyahProgress(
    val surahNumber: Int,
    val ayahNumber: Int,
    val status: MemorisationStatus,
    val repetitions: Int,
    val easinessFactor: Double,
    val intervalDays: Int,
    val lastReviewedAt: Instant?,
    val nextReviewAt: Instant?,
    val lastAccuracy: Int?,
)
