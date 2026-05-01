package com.example.mob_dev_portfolio.core.result

/** Result envelope used by domain and UI code. */
sealed class DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Error(val error: Throwable) : DomainResult<Nothing>()
    data object Loading : DomainResult<Nothing>()
}
