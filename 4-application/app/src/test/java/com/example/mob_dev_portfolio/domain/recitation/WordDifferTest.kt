package com.example.mob_dev_portfolio.domain.recitation

import com.example.mob_dev_portfolio.domain.model.MistakeType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordDifferTest {

    @Test
    fun `identical input yields no mistakes`() {
        val mistakes = WordDiffer.diff("بسم الله", "بسم الله")
        assertThat(mistakes).isEmpty()
    }

    @Test
    fun `missing word recorded with expected word and null actual`() {
        val mistakes = WordDiffer.diffTokens(listOf("a", "b", "c"), listOf("a", "c"))
        assertThat(mistakes).hasSize(1)
        with(mistakes.single()) {
            assertThat(type).isEqualTo(MistakeType.MISSING)
            assertThat(expectedWord).isEqualTo("b")
            assertThat(actualWord).isNull()
            assertThat(expectedIndex).isEqualTo(1)
        }
    }

    @Test
    fun `extra word recorded with actual word and null expected`() {
        val mistakes = WordDiffer.diffTokens(listOf("a", "b"), listOf("a", "x", "b"))
        assertThat(mistakes).hasSize(1)
        with(mistakes.single()) {
            assertThat(type).isEqualTo(MistakeType.EXTRA)
            assertThat(actualWord).isEqualTo("x")
            assertThat(expectedWord).isNull()
        }
    }

    @Test
    fun `substituted word recorded with both expected and actual`() {
        val mistakes = WordDiffer.diffTokens(listOf("a", "b"), listOf("a", "z"))
        assertThat(mistakes).hasSize(1)
        with(mistakes.single()) {
            assertThat(type).isEqualTo(MistakeType.SUBSTITUTED)
            assertThat(expectedWord).isEqualTo("b")
            assertThat(actualWord).isEqualTo("z")
            assertThat(expectedIndex).isEqualTo(1)
        }
    }

    @Test
    fun `mistakes returned in expected text order`() {
        val mistakes = WordDiffer.diffTokens(listOf("a", "b", "c", "d"), listOf("a", "z", "y"))
        // The DP yields three edits; precise mistake types depend on the
        // tie-break order, but the result is always sorted by expectedIndex.
        val indices = mistakes.map { it.expectedIndex }
        assertThat(indices).isInOrder()
        assertThat(mistakes).hasSize(3)
    }

    @Test
    fun `empty actual yields all missing`() {
        val mistakes = WordDiffer.diffTokens(listOf("a", "b"), emptyList())
        assertThat(mistakes.map { it.type }).containsExactly(MistakeType.MISSING, MistakeType.MISSING)
    }

    @Test
    fun `empty expected yields all extra`() {
        val mistakes = WordDiffer.diffTokens(emptyList(), listOf("a", "b"))
        assertThat(mistakes.map { it.type }).containsExactly(MistakeType.EXTRA, MistakeType.EXTRA)
    }

    @Test
    fun `normalises diacritics so identical-after-normalisation strings match`() {
        val mistakes = WordDiffer.diff("بِسْمِ", "بسم")
        assertThat(mistakes).isEmpty()
    }
}
