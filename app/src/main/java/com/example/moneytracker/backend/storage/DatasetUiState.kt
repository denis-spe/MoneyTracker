// Glory be the LORD of hosts
package com.example.moneytracker.backend.storage

sealed interface DatasetUiState {
    object Loading : DatasetUiState
    object Success : DatasetUiState
    data class Error(val message: String?) : DatasetUiState
}