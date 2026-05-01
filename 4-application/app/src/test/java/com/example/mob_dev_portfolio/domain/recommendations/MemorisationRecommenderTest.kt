package com.example.mob_dev_portfolio.domain.recommendations

import com.example.mob_dev_portfolio.domain.model.AyahProgress
import com.example.mob_dev_portfolio.domain.model.MemorisationStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class MemorisationRecommenderTest {

    private val now: Instant = Instant.parse("2026-04-29T10:00:00Z")
    private val recommender = MemorisationRecommender()

    private fun progress(
        surah: Int,
        ayah: Int,
        status: MemorisationStatus = MemorisationStatus.REVIEWING,
        lastAccuracy: Int? = null,
        nextReviewAt: Instant? = null,
    ) = AyahProgress(
        surahNumber = surah,
        ayahNumber = ayah,
        status = status,
        repetitions = 1,
        easinessFactor = 2.5,
        intervalDays = 1,
        lastReviewedAt = now,
        nextReviewAt = nextReviewAt,
        lastAccuracy = lastAccuracy,
    )

    @Test
    fun `overdue ayahs ranked above weak`() {
        val items = listOf(
            progress(1, 1, lastAccuracy = 50),
            progress(2, 2, nextReviewAt = now.minus(3, ChronoUnit.DAYS)),
        )
        val results = recommender.recommend(items, emptyList(), now)
        assertThat(results.first().surahNumber).isEqualTo(2)
    }

    @Test
    fun `weak ayahs surface when nothing is overdue`() {
        val items = listOf(progress(1, 1, lastAccuracy = 30))
        val results = recommender.recommend(items, emptyList(), now)
        assertThat(results).hasSize(1)
        assertThat(results.first().reason).startsWith("Weak")
    }

    @Test
    fun `new suggestions used as fillers when nothing else qualifies`() {
        val results = recommender.recommend(emptyList(), listOf(1 to 1, 1 to 2), now)
        assertThat(results.map { it.surahNumber to it.ayahNumber })
            .containsExactly(1 to 1, 1 to 2).inOrder()
    }

    @Test
    fun `new status items are skipped`() {
        val items = listOf(progress(1, 1, status = MemorisationStatus.NEW, lastAccuracy = 10))
        val results = recommender.recommend(items, emptyList(), now)
        assertThat(results).isEmpty()
    }

    @Test
    fun `result list capped at maxResults`() {
        val items = (1..20).map { progress(1, it, lastAccuracy = 30) }
        val results = recommender.recommend(items, emptyList(), now, maxResults = 3)
        assertThat(results).hasSize(3)
    }
}
