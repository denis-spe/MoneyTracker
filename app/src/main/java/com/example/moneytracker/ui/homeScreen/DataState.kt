// Bless be the name of the LORD, For his mercy endures forever,
// And his faithfulness to all generations.
package com.example.moneytracker.ui.homeScreen

// 1. Define a simple wrapper for the specific data state
sealed interface DataState<out T> {
    data object Loading : DataState<Nothing>
    data class Success<T>(val data: T) : DataState<T>
    data class Error(val exception: Throwable) : DataState<Nothing>
}