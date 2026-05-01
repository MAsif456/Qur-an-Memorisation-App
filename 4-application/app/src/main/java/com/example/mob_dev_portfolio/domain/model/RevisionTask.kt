package com.example.mob_dev_portfolio.domain.model

import java.time.Instant

/** An ayah surfaced as due. `overdueDays` is negative if not yet due. */
data class RevisionTask(
    val surahNumber: Int,
    val ayahNumber: Int,
    val dueAt: Instant,
    val overdueDays: Int,
)
