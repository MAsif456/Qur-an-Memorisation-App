package com.example.mob_dev_portfolio.domain.model

/**
 * A single mistake from [com.example.mob_dev_portfolio.domain.recitation.WordDiffer].
 *
 * For an [MistakeType.EXTRA], `expectedIndex` is the insertion point: the
 * index of the expected word the extra appeared just before, or
 * `expected.size` if it appeared at the end.
 */
data class Mistake(
    val type: MistakeType,
    val expectedIndex: Int,
    val expectedWord: String?,
    val actualWord: String?,
)
