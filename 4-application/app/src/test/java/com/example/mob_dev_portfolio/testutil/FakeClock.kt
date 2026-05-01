package com.example.mob_dev_portfolio.testutil

import com.example.mob_dev_portfolio.core.time.Clock
import java.time.Instant

/** Mutable clock for tests; advance [now] to simulate the passage of time. */
class FakeClock(var now: Instant = Instant.parse("2026-04-29T10:00:00Z")) : Clock {
    override fun now(): Instant = now
}
