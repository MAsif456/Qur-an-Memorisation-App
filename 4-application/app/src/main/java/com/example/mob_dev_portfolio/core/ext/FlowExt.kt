package com.example.mob_dev_portfolio.core.ext

import com.example.mob_dev_portfolio.core.result.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Wraps an upstream flow into [NetworkResult] envelopes: `Loading` first, then
 * a `Success` per upstream value, or one `Error` if the upstream throws.
 */
fun <T> Flow<T>.asNetworkResult(
    dispatcher: CoroutineContext = EmptyCoroutineContext,
): Flow<NetworkResult<T>> = this
    .map<T, NetworkResult<T>> { NetworkResult.Success(it) }
    .onStart { emit(NetworkResult.Loading) }
    .catch { emit(NetworkResult.Error(it)) }
    .flowOn(dispatcher)

/** One-shot [NetworkResult] flow built from a suspending block. */
fun <T> networkFlow(block: suspend () -> T): Flow<NetworkResult<T>> = flow {
    emit(NetworkResult.Loading)
    runCatching { block() }
        .onSuccess { emit(NetworkResult.Success(it)) }
        .onFailure { emit(NetworkResult.Error(it)) }
}
