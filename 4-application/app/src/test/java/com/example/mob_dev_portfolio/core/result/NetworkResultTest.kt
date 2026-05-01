package com.example.mob_dev_portfolio.core.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class NetworkResultTest {

    @Test
    fun `success variant contains correct data`() {
        val result = NetworkResult.Success("Test")
        assertEquals("Test", result.data)
    }

    @Test
    fun `error variant contains correct exception`() {
        val exception = IOException("Network failing")
        val result = NetworkResult.Error(exception)
        assertEquals(exception, result.error)
    }

    @Test
    fun `loading variant is a singleton object`() {
        val result: NetworkResult<String> = NetworkResult.Loading
        assertTrue(result is NetworkResult.Loading)
    }
}
