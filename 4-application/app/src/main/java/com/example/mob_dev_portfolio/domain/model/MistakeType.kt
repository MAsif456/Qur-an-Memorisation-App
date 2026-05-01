package com.example.mob_dev_portfolio.domain.model

/**
 *  - [MISSING]: the user skipped a word that was expected.
 *  - [EXTRA]: the user spoke a word that was not expected at this position.
 *  - [SUBSTITUTED]: the user said a different word in place of the expected one.
 */
enum class MistakeType {
    MISSING,
    EXTRA,
    SUBSTITUTED,
}
