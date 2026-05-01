package com.example.mob_dev_portfolio.core.time

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ClockTest {

    @Test
    fun `system clock implementation generates current instant`() {
        val clock = SystemClock()
        val before = Instant.now()
        val now = clock.now()
        val after = Instant.now()

        // Assert that the generated time is strictly between the bounds.
        assertTrue(now == before || now.isAfter(before))
        assertTrue(now == after || now.isBefore(after))
    }
}
