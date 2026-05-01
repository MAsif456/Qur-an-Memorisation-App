package com.example.mob_dev_portfolio.domain.recitation

import com.example.mob_dev_portfolio.domain.model.Mistake
import com.example.mob_dev_portfolio.domain.model.MistakeType

/**
 * Word-level differ.
 *
 * Tokenises both inputs through [ArabicNormaliser.words], builds the full
 * Levenshtein DP matrix, then walks back to emit one [Mistake] per edit:
 * diagonal mismatch is SUBSTITUTED, horizontal step is EXTRA, vertical step
 * is MISSING. Mistakes are returned in expected-text order.
 */
object WordDiffer {

    fun diff(expected: String, actual: String): List<Mistake> {
        val expectedTokens = ArabicNormaliser.words(expected)
        val actualTokens = ArabicNormaliser.words(actual)
        return diffTokens(expectedTokens, actualTokens)
    }

    fun diffTokens(expected: List<String>, actual: List<String>): List<Mistake> {
        val n = expected.size
        val m = actual.size
        val dp = Array(n + 1) { IntArray(m + 1) }
        for (i in 0..n) dp[i][0] = i
        for (j in 0..m) dp[0][j] = j
        for (i in 1..n) for (j in 1..m) {
            val sub = if (expected[i - 1] == actual[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1,
                dp[i - 1][j - 1] + sub,
            )
        }

        val mistakes = ArrayDeque<Mistake>()
        var i = n
        var j = m
        while (i > 0 || j > 0) {
            when {
                i > 0 && j > 0 && expected[i - 1] == actual[j - 1] -> {
                    i--
                    j--
                }
                i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1 -> {
                    mistakes.addFirst(
                        Mistake(MistakeType.SUBSTITUTED, i - 1, expected[i - 1], actual[j - 1]),
                    )
                    i--
                    j--
                }
                j > 0 && (i == 0 || dp[i][j] == dp[i][j - 1] + 1) -> {
                    mistakes.addFirst(
                        Mistake(MistakeType.EXTRA, i, null, actual[j - 1]),
                    )
                    j--
                }
                else -> {
                    mistakes.addFirst(
                        Mistake(MistakeType.MISSING, i - 1, expected[i - 1], null),
                    )
                    i--
                }
            }
        }
        return mistakes.toList()
    }
}
