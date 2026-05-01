package com.example.mob_dev_portfolio.domain.streaks

import com.example.mob_dev_portfolio.domain.model.Streak
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max

/**
 * Streak update logic. Days are compared as user-local [LocalDate], so a
 * review at 23:55 Tue and one at 00:05 Wed count as two distinct days.
 */
object StreakCalculator {

    fun update(previous: Streak, today: LocalDate): Streak {
        val last = previous.lastActiveDate
        return when {
            last == null -> Streak(current = 1, longest = max(previous.longest, 1), lastActiveDate = today)
            last == today -> previous
            ChronoUnit.DAYS.between(last, today) == 1L -> {
                val nextCurrent = previous.current + 1
                Streak(
                    current = nextCurrent,
                    longest = max(previous.longest, nextCurrent),
                    lastActiveDate = today,
                )
            }
            else -> Streak(
                current = 1,
                longest = max(previous.longest, 1),
                lastActiveDate = today,
            )
        }
    }

    /** 0 if the persisted streak has lapsed (no review today or yesterday). */
    fun effective(streak: Streak, today: LocalDate): Int {
        val last = streak.lastActiveDate ?: return 0
        return if (ChronoUnit.DAYS.between(last, today) <= 1L) streak.current else 0
    }
}
