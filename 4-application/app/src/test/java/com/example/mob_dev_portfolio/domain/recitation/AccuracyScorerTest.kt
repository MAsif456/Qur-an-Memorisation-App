package com.example.mob_dev_portfolio.domain.recitation

import com.example.mob_dev_portfolio.domain.model.Mistake
import com.example.mob_dev_portfolio.domain.model.MistakeType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AccuracyScorerTest {

    @Test
    fun `zero word count returns max score`() {
        val score = AccuracyScorer.score(0, emptyList())
        assertThat(score.value).isEqualTo(100)
    }

    @Test
    fun `no mistakes returns max score`() {
        val score = AccuracyScorer.score(10, emptyList())
        assertThat(score.value).isEqualTo(100)
    }

    @Test
    fun `equal mistakes and words returns zero`() {
        val mistakes = List(5) { Mistake(MistakeType.MISSING, it, "x", null) }
        val score = AccuracyScorer.score(5, mistakes)
        assertThat(score.value).isEqualTo(0)
    }

    @Test
    fun `more mistakes than words clamps to zero`() {
        val mistakes = List(20) { Mistake(MistakeType.EXTRA, 0, null, "x") }
        val score = AccuracyScorer.score(5, mistakes)
        assertThat(score.value).isEqualTo(0)
    }

    @Test
    fun `partial mistakes return rounded score`() {
        // 2 mistakes out of 10 words → 80%.
        val mistakes = listOf(
            Mistake(MistakeType.MISSING, 0, "a", null),
            Mistake(MistakeType.SUBSTITUTED, 1, "b", "z"),
        )
        val score = AccuracyScorer.score(10, mistakes)
        assertThat(score.value).isEqualTo(80)
    }

    @Test
    fun `single mistake on three word ayah rounds appropriately`() {
        val mistakes = listOf(Mistake(MistakeType.MISSING, 0, "a", null))
        val score = AccuracyScorer.score(3, mistakes)
        // 1 - 1/3 = 0.667 → 67
        assertThat(score.value).isEqualTo(67)
    }
}
