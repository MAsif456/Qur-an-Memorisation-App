package com.example.mob_dev_portfolio.domain.recitation

/**
 * Strips diacritics, tatweel, and folds hamza, ta marbouta, and alif
 * maqsura. Without this the recogniser transcript and the cached Uthmani
 * text produce huge spurious diffs.
 */
object ArabicNormaliser {

    private val DIACRITICS = ('ً'..'ْ').toSet() +
        ('ۖ'..'ۭ').toSet() +
        setOf('ٰ', 'ـ')

    // Common orthographic divergences that should not count as substitutions.
    private val LETTER_FOLDING: Map<Char, Char> = mapOf(
        'أ' to 'ا',
        'إ' to 'ا',
        'آ' to 'ا',
        'ٱ' to 'ا',
        'ة' to 'ه',
        'ى' to 'ي',
    )

    private val WHITESPACE_RE = Regex("\\s+")

    fun normalise(input: String): String {
        if (input.isEmpty()) return input
        val builder = StringBuilder(input.length)
        for (ch in input) {
            if (ch in DIACRITICS) continue
            builder.append(LETTER_FOLDING[ch] ?: ch)
        }
        return builder.toString()
            .replace(WHITESPACE_RE, " ")
            .trim()
    }

    fun words(input: String): List<String> {
        val cleaned = normalise(input)
        if (cleaned.isEmpty()) return emptyList()
        return cleaned.split(' ').filter { it.isNotEmpty() }
    }
}
