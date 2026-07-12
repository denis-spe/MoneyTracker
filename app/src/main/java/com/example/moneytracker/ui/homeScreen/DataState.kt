// Glory to the LORD our GOD
package com.example.moneytracker.ui.homeScreen

import androidx.compose.runtime.Stable

// 1. Define a simple wrapper for the specific data state
@Stable
sealed interface DataState<out T> {
    data object Loading : DataState<Nothing>
    data class Success<T>(val data: T) : DataState<T>
    data class Error(val exception: Throwable) : DataState<Nothing>
}

inline fun <T, R> DataState<T>.map(transform: (T) -> R): DataState<R> {
    return when (this) {
        is DataState.Loading -> DataState.Loading
        is DataState.Success -> DataState.Success(transform(data))
        is DataState.Error -> DataState.Error(exception)
    }
}
