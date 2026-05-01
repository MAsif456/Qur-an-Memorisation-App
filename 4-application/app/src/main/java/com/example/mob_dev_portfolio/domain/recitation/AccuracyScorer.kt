package com.example.mob_dev_portfolio.domain.recitation

import com.example.mob_dev_portfolio.domain.model.AccuracyScore
import com.example.mob_dev_portfolio.domain.model.Mistake
import kotlin.math.roundToInt

/**
 * Turns mistake count into a 0..100 score.
 *
 * Formula is `100 * (1 - mistakes/words)`, rounded and clamped. Generous on
 * purpose so a single dropped word on a long ayah doesn't tank the score.
 */
object AccuracyScorer {

    private const val MAX: Int = 100

    fun score(expectedWordCount: Int, mistakes: List<Mistake>): AccuracyScore {
        if (expectedWordCount <= 0) return AccuracyScore(MAX)
        val ratio = mistakes.size.toDouble() / expectedWordCount.toDouble()
        val raw = ((1.0 - ratio) * MAX).roundToInt()
        return AccuracyScore.coerced(raw)
    }
}
