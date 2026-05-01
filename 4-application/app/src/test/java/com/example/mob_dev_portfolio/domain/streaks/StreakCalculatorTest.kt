package com.example.mob_dev_portfolio.domain.streaks

import com.example.mob_dev_portfolio.domain.model.Streak
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 4, 29)

    @Test
    fun `first ever review starts the streak at one`() {
        val updated = StreakCalculator.update(Streak(0, 0, null), today)
        assertThat(updated.current).isEqualTo(1)
        assertThat(updated.longest).isEqualTo(1)
        assertThat(updated.lastActiveDate).isEqualTo(today)
    }

    @Test
    fun `same day review is a no-op`() {
        val initial = Streak(3, 5, today)
        val updated = StreakCalculator.update(initial, today)
        assertThat(updated).isEqualTo(initial)
    }

    @Test
    fun `consecutive day increments current and bumps longest if exceeded`() {
        val updated = StreakCalculator.update(Streak(2, 5, today.minusDays(1)), today)
        assertThat(updated.current).isEqualTo(3)
        assertThat(updated.longest).isEqualTo(5)
    }

    @Test
    fun `consecutive day past longest updates longest`() {
        val updated = StreakCalculator.update(Streak(5, 5, today.minusDays(1)), today)
        assertThat(updated.current).isEqualTo(6)
        assertThat(updated.longest).isEqualTo(6)
    }

    @Test
    fun `gap of two days resets current to one but preserves longest`() {
        val updated = StreakCalculator.update(Streak(7, 7, today.minusDays(2)), today)
        assertThat(updated.current).isEqualTo(1)
        assertThat(updated.longest).isEqualTo(7)
    }

    @Test
    fun `effective returns zero when last activity older than yesterday`() {
        val streak = Streak(7, 7, today.minusDays(2))
        assertThat(StreakCalculator.effective(streak, today)).isEqualTo(0)
    }

    @Test
    fun `effective returns current when reviewed yesterday`() {
        val streak = Streak(7, 7, today.minusDays(1))
        assertThat(StreakCalculator.effective(streak, today)).isEqualTo(7)
    }

    @Test
    fun `effective returns zero when no last activity`() {
        val streak = Streak(0, 0, null)
        assertThat(StreakCalculator.effective(streak, today)).isEqualTo(0)
    }
}
