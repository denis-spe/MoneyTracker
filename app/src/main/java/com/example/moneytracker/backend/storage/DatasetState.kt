// Glory be the LORD of hosts
package com.example.moneytracker.backend.storage

import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.usecase.HomeData

sealed interface DatasetState {
    object Loading : DatasetState
    object Success : DatasetState
    data class Error(val message: String?) : DatasetState
}

// Add this once, e.g. in a DataStateExt.kt file
fun <T> HomeData.toDataState(transform: (List<FinanceEntity>) -> T): DataState<T> =
    when (datasetState) {
        is DatasetState.Loading -> DataState.Loading
        is DatasetState.Error -> DataState.Error(Throwable(datasetState.message))
        is DatasetState.Success -> DataState.Success(transform(datasets))
    }