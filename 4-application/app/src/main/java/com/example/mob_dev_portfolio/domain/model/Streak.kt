package com.example.mob_dev_portfolio.domain.model

import java.time.LocalDate

/**
 * Streak shown on the Home screen.
 *
 * `lastActiveDate` is null when the user has never reviewed anything.
 */
data class Streak(
    val current: Int,
    val longest: Int,
    val lastActiveDate: LocalDate?,
)
