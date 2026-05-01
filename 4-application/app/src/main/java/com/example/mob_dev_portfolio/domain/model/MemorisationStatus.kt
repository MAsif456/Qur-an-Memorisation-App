package com.example.mob_dev_portfolio.domain.model

/**
 * Lifecycle of an ayah in the user's memorisation journey.
 *
 *  - [NEW]: never seen by the user.
 *  - [LEARNING]: marked as memorised but still inside the SM-2 ramp-up
 *    (intervals < 7 days, repetitions < 3).
 *  - [REVIEWING]: stable in the schedule.
 *  - [MASTERED]: long-interval, high-easiness; surfaced rarely.
 */
enum class MemorisationStatus {
    NEW,
    LEARNING,
    REVIEWING,
    MASTERED,
}
