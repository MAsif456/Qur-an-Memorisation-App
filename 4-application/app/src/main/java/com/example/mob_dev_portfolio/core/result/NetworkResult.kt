package com.example.mob_dev_portfolio.core.result

/** Result envelope used by the network layer. */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val error: Throwable) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}
