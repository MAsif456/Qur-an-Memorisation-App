package com.example.mob_dev_portfolio.core.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainResultTest {

    @Test
    fun `success variant contains correct data`() {
        val result = DomainResult.Success("Test")
        assertEquals("Test", result.data)
    }

    @Test
    fun `error variant contains correct exception`() {
        val exception = IllegalArgumentException("Invalid input")
        val result = DomainResult.Error(exception)
        assertEquals(exception, result.error)
    }

    @Test
    fun `loading variant is a singleton object`() {
        val result: DomainResult<String> = DomainResult.Loading
        assertTrue(result is DomainResult.Loading)
    }
}
