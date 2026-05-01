package com.example.mob_dev_portfolio.domain.model

/**
 * 0..100 accuracy from [com.example.mob_dev_portfolio.domain.recitation.AccuracyScorer].
 *
 * Wrapping the raw integer prevents accidental mixing with other percentages.
 */
@JvmInline
value class AccuracyScore(val value: Int) {

    init {
        require(value in MIN..MAX) {
            "AccuracyScore must be in [$MIN, $MAX], was $value"
        }
    }

    companion object {
        const val MIN: Int = 0
        const val MAX: Int = 100

        /** Clamps any input into the valid range. */
        fun coerced(raw: Int): AccuracyScore = AccuracyScore(raw.coerceIn(MIN, MAX))
    }
}
