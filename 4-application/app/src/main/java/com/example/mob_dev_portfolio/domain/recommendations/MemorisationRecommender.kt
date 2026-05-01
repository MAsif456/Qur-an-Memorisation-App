package com.example.mob_dev_portfolio.domain.recommendations

import com.example.mob_dev_portfolio.domain.model.AyahProgress
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.example.mob_dev_portfolio.domain.model.Recommendation
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.max

/**
 * Ranks ayahs for the Home "Recommended for you" list.
 *
 * Score = overdueDays * OVERDUE_WEIGHT + weakDelta * WEAK_WEIGHT.
 * `newSuggestions` are used as fillers when nothing else qualifies.
 */
class MemorisationRecommender {

    fun recommend(
        progress: List<AyahProgress>,
        newSuggestions: List<Pair<Int, Int>>,
        now: Instant,
        weakThreshold: Int = DEFAULT_WEAK_THRESHOLD,
        maxResults: Int = DEFAULT_MAX_RESULTS,
    ): List<Recommendation> {
        val ranked = mutableListOf<Recommendation>()

        for (item in progress) {
            if (item.status == MemorisationStatus.NEW) continue
            val overdueDays = item.nextReviewAt?.let {
                ChronoUnit.DAYS.between(it, now).toInt().coerceAtLeast(0)
            } ?: 0
            val accuracy = item.lastAccuracy ?: 100
            val weakDelta = max(0, weakThreshold - accuracy)
            val score = overdueDays * OVERDUE_WEIGHT + weakDelta * WEAK_WEIGHT
            if (score <= 0.0) continue
            val reason = if (overdueDays * OVERDUE_WEIGHT >= weakDelta * WEAK_WEIGHT) {
                "Overdue $overdueDays day${if (overdueDays == 1) "" else "s"}"
            } else {
                "Weak: $accuracy%% last attempt"
            }
            ranked += Recommendation(item.surahNumber, item.ayahNumber, reason, score)
        }

        if (ranked.size < maxResults) {
            for ((surah, ayah) in newSuggestions) {
                if (ranked.size >= maxResults) break
                ranked += Recommendation(surah, ayah, "New", NEW_WEIGHT)
            }
        }

        val ordering = compareByDescending<Recommendation> { it.score }
            .thenBy { it.surahNumber }
            .thenBy { it.ayahNumber }
        return ranked.sortedWith(ordering).take(maxResults)
    }

    companion object {
        const val DEFAULT_WEAK_THRESHOLD: Int = 70
        const val DEFAULT_MAX_RESULTS: Int = 5

        // A single day overdue should outweigh a 10-point weakness delta so the
        // schedule drives recommendations more strongly than recitation accuracy.
        const val OVERDUE_WEIGHT: Double = 5.0
        const val WEAK_WEIGHT: Double = 0.4
        const val NEW_WEIGHT: Double = 0.5
    }
}
