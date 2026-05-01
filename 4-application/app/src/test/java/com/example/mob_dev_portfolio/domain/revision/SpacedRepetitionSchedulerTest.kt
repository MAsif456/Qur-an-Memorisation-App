package com.example.mob_dev_portfolio.domain.revision

import com.example.mob_dev_portfolio.domain.model.AyahProgress
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class SpacedRepetitionSchedulerTest {

    private lateinit var scheduler: SpacedRepetitionScheduler
    private val now: Instant = Instant.parse("2026-04-29T10:00:00Z")

    @Before
    fun setUp() {
        scheduler = SpacedRepetitionScheduler()
    }

    private fun fresh() = AyahProgress(
        surahNumber = 1,
        ayahNumber = 1,
        status = MemorisationStatus.LEARNING,
        repetitions = 0,
        easinessFactor = 2.5,
        intervalDays = 0,
        lastReviewedAt = null,
        nextReviewAt = null,
        lastAccuracy = null,
    )

    @Test
    fun `first successful review schedules one day out`() {
        val next = scheduler.next(fresh(), accuracy = 95, reviewedAt = now)
        assertThat(next.repetitions).isEqualTo(1)
        assertThat(next.intervalDays).isEqualTo(1)
        assertThat(next.nextReviewAt).isEqualTo(now.plus(1L, ChronoUnit.DAYS))
        assertThat(next.lastAccuracy).isEqualTo(95)
    }

    @Test
    fun `second successful review jumps to six days`() {
        val first = scheduler.next(fresh(), 95, now)
        val second = scheduler.next(first, 95, now.plus(1, ChronoUnit.DAYS))
        assertThat(second.repetitions).isEqualTo(2)
        assertThat(second.intervalDays).isEqualTo(6)
    }

    @Test
    fun `failed review resets repetitions and schedules tomorrow`() {
        val first = scheduler.next(fresh(), 95, now)
        val failed = scheduler.next(first.copy(intervalDays = 6), 30, now.plus(1, ChronoUnit.DAYS))
        assertThat(failed.repetitions).isEqualTo(0)
        assertThat(failed.intervalDays).isEqualTo(1)
    }

    @Test
    fun `easiness factor stays within bounds`() {
        var current = fresh()
        repeat(20) {
            current = scheduler.next(current, accuracy = 100, reviewedAt = now)
        }
        assertThat(current.easinessFactor).isAtMost(2.5)
        assertThat(current.easinessFactor).isAtLeast(1.3)
    }

    @Test
    fun `mastered status emerges after long intervals`() {
        var current = fresh()
        repeat(10) {
            current = scheduler.next(current, accuracy = 100, reviewedAt = now)
        }
        assertThat(current.status).isAnyOf(MemorisationStatus.MASTERED, MemorisationStatus.REVIEWING)
    }

    @Test
    fun `learning status holds until reps exceed threshold`() {
        val first = scheduler.next(fresh(), 95, now)
        assertThat(first.status).isEqualTo(MemorisationStatus.LEARNING)
    }
}
