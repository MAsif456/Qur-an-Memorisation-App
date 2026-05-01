package com.example.mob_dev_portfolio.core.ext

import app.cash.turbine.test
import com.example.mob_dev_portfolio.core.result.NetworkResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FlowExtTest {

    @Test
    fun `asNetworkResult emits Loading then Success for each value`() = runTest {
        flowOf(1, 2, 3).asNetworkResult().test {
            assertThat(awaitItem()).isEqualTo(NetworkResult.Loading)
            assertThat(awaitItem()).isEqualTo(NetworkResult.Success(1))
            assertThat(awaitItem()).isEqualTo(NetworkResult.Success(2))
            assertThat(awaitItem()).isEqualTo(NetworkResult.Success(3))
            awaitComplete()
        }
    }

    @Test
    fun `asNetworkResult emits only Loading when upstream is empty`() = runTest {
        flowOf<Int>().asNetworkResult().test {
            assertThat(awaitItem()).isEqualTo(NetworkResult.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `asNetworkResult emits Error when upstream throws`() = runTest {
        val boom = IllegalStateException("boom")
        flow<Int> { throw boom }.asNetworkResult().test {
            assertThat(awaitItem()).isEqualTo(NetworkResult.Loading)
            val terminal = awaitItem()
            assertThat(terminal).isInstanceOf(NetworkResult.Error::class.java)
            assertThat((terminal as NetworkResult.Error).error).isSameInstanceAs(boom)
            awaitComplete()
        }
    }

    @Test
    fun `networkFlow emits Loading then Success`() = runTest {
        networkFlow { 42 }.test {
            assertThat(awaitItem()).isEqualTo(NetworkResult.Loading)
            assertThat(awaitItem()).isEqualTo(NetworkResult.Success(42))
            awaitComplete()
        }
    }

    @Test
    fun `networkFlow emits Loading then Error when block throws`() = runTest {
        val boom = RuntimeException("boom")
        networkFlow { throw boom }.test {
            assertThat(awaitItem()).isEqualTo(NetworkResult.Loading)
            val terminal = awaitItem()
            assertThat(terminal).isInstanceOf(NetworkResult.Error::class.java)
            assertThat((terminal as NetworkResult.Error).error).isSameInstanceAs(boom)
            awaitComplete()
        }
    }
}
