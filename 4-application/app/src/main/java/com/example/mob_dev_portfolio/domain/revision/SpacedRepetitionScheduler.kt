package com.example.mob_dev_portfolio.domain.revision

import com.example.mob_dev_portfolio.domain.model.AyahProgress
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * SM-2-inspired spaced-repetition scheduler.
 *
 * Two adaptations from textbook SM-2:
 *  1. Grade is derived from accuracy (>=90 -> 5, >=75 -> 4, >=60 -> 3,
 *     otherwise treated as forgotten and the interval resets).
 *  2. Status is derived from repetitions and the resulting interval so the
 *     UI doesn't need a separate field.
 */
class SpacedRepetitionScheduler {

    fun next(
        previous: AyahProgress,
        accuracy: Int,
        reviewedAt: Instant,
    ): AyahProgress {
        val grade = accuracy.toGrade()
        val newRepetitions: Int
        val newInterval: Int
        val newEf: Double

        if (grade < PASS_THRESHOLD) {
            newRepetitions = 0
            newInterval = 1
            newEf = previous.easinessFactor.coerceAtLeast(MIN_EF)
        } else {
            newRepetitions = previous.repetitions + 1
            newEf = updateEf(previous.easinessFactor, grade)
            newInterval = when (newRepetitions) {
                1 -> 1
                2 -> SECOND_INTERVAL_DAYS
                else -> (previous.intervalDays * newEf).toInt().coerceAtLeast(SECOND_INTERVAL_DAYS)
            }
        }

        val nextReview = reviewedAt.plus(newInterval.toLong(), ChronoUnit.DAYS)
        return previous.copy(
            status = derive(newRepetitions, newInterval),
            repetitions = newRepetitions,
            easinessFactor = newEf,
            intervalDays = newInterval,
            lastReviewedAt = reviewedAt,
            nextReviewAt = nextReview,
            lastAccuracy = accuracy,
        )
    }

    private fun Int.toGrade(): Int = when {
        this >= GRADE_5_MIN -> 5
        this >= GRADE_4_MIN -> 4
        this >= GRADE_3_MIN -> 3
        this >= GRADE_2_MIN -> 2
        this > 0 -> 1
        else -> 0
    }

    private fun updateEf(previous: Double, grade: Int): Double {
        val updated = previous + (EF_CONST_A - (FIVE - grade) * (EF_CONST_B + (FIVE - grade) * EF_CONST_C))
        return updated.coerceIn(MIN_EF, MAX_EF)
    }

    private fun derive(repetitions: Int, intervalDays: Int): MemorisationStatus = when {
        repetitions < LEARNING_REPS_MAX || intervalDays < LEARNING_INTERVAL_MAX -> MemorisationStatus.LEARNING
        intervalDays >= MASTERED_INTERVAL_MIN -> MemorisationStatus.MASTERED
        else -> MemorisationStatus.REVIEWING
    }

    private companion object {
        const val PASS_THRESHOLD = 3
        const val SECOND_INTERVAL_DAYS = 6
        const val MIN_EF = 1.3
        const val MAX_EF = 2.5
        const val FIVE = 5

        const val EF_CONST_A = 0.1
        const val EF_CONST_B = 0.08
        const val EF_CONST_C = 0.02

        const val GRADE_5_MIN = 90
        const val GRADE_4_MIN = 75
        const val GRADE_3_MIN = 60
        const val GRADE_2_MIN = 40

        const val LEARNING_REPS_MAX = 3
        const val LEARNING_INTERVAL_MAX = 7
        const val MASTERED_INTERVAL_MIN = 30
    }
}
