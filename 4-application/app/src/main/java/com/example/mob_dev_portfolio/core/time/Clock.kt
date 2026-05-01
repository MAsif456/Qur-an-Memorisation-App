package com.example.mob_dev_portfolio.core.time

import java.time.Instant

/** Indirection over `Instant.now()` so tests can inject a fake clock. */
interface Clock {
    fun now(): Instant
}

class SystemClock : Clock {
    override fun now(): Instant = Instant.now()
}
