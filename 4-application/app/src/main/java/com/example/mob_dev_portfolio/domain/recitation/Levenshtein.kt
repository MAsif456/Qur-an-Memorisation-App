package com.example.mob_dev_portfolio.domain.recitation

import kotlin.math.min

/**
 * Word-level Levenshtein distance with a configurable cost model.
 *
 * Uses two rolling rows so memory is O(min(|a|, |b|)) rather than the full
 * matrix. [WordDiffer] uses this only as a sanity check; the diff itself
 * needs the full matrix to walk the edit path.
 */
object Levenshtein {

    data class Costs(
        val insert: Int = 1,
        val delete: Int = 1,
        val substitute: Int = 1,
    )

    fun distance(
        expected: List<String>,
        actual: List<String>,
        costs: Costs = Costs(),
    ): Int {
        if (expected.isEmpty()) return actual.size * costs.insert
        if (actual.isEmpty()) return expected.size * costs.delete

        var previous = IntArray(actual.size + 1) { it * costs.insert }
        var current = IntArray(actual.size + 1)

        for (i in 1..expected.size) {
            current[0] = i * costs.delete
            for (j in 1..actual.size) {
                val sub = if (expected[i - 1] == actual[j - 1]) 0 else costs.substitute
                current[j] = min(
                    min(current[j - 1] + costs.insert, previous[j] + costs.delete),
                    previous[j - 1] + sub,
                )
            }
            val swap = previous
            previous = current
            current = swap
        }
        return previous[actual.size]
    }
}
