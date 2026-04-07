// Glory be the LORD of hosts
package com.example.moneytracker.backend.storage

sealed interface DatasetState {
    object Loading : DatasetState
    object Success : DatasetState
    data class Error(val message: String?) : DatasetState
}