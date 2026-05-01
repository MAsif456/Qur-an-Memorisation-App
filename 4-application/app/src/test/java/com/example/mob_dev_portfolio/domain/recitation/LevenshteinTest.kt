package com.example.mob_dev_portfolio.domain.recitation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LevenshteinTest {

    @Test
    fun `equal lists give zero distance`() {
        val d = Levenshtein.distance(listOf("a", "b", "c"), listOf("a", "b", "c"))
        assertThat(d).isEqualTo(0)
    }

    @Test
    fun `empty expected costs one insert per actual word`() {
        val d = Levenshtein.distance(emptyList(), listOf("x", "y"))
        assertThat(d).isEqualTo(2)
    }

    @Test
    fun `empty actual costs one delete per expected word`() {
        val d = Levenshtein.distance(listOf("x", "y"), emptyList())
        assertThat(d).isEqualTo(2)
    }

    @Test
    fun `single substitution costs one`() {
        val d = Levenshtein.distance(listOf("a", "b"), listOf("a", "z"))
        assertThat(d).isEqualTo(1)
    }

    @Test
    fun `single insertion costs one`() {
        val d = Levenshtein.distance(listOf("a", "b"), listOf("a", "x", "b"))
        assertThat(d).isEqualTo(1)
    }

    @Test
    fun `single deletion costs one`() {
        val d = Levenshtein.distance(listOf("a", "x", "b"), listOf("a", "b"))
        assertThat(d).isEqualTo(1)
    }

    @Test
    fun `cost model favours substitution when configured`() {
        val cheap = Levenshtein.distance(
            listOf("a"),
            listOf("b"),
            Levenshtein.Costs(insert = 5, delete = 5, substitute = 1),
        )
        assertThat(cheap).isEqualTo(1)
    }

    @Test
    fun `unicode tokens compared correctly`() {
        val d = Levenshtein.distance(listOf("بسم", "الله"), listOf("بسم", "الله"))
        assertThat(d).isEqualTo(0)
    }
}
