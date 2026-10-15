package com.employeedirectory.app.domain.common

/**
 * Generic outcome wrapper so the data layer never leaks raw exceptions or Retrofit types
 * into domain/presentation. Named DataResult to avoid clashing with kotlin.Result.
 */
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : DataResult<Nothing>()
}
