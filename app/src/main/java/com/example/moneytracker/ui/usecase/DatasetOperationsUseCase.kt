package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Finance
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import javax.inject.Inject

class FinanceOperationsUseCase @Inject constructor(
    private val dataStorage: DataStorage
) {

    suspend fun addData(userId: String, finance: Finance): String {
        return dataStorage.addData(userId, finance)
    }

    suspend fun filterFinances(
        userId: String,
        filter: Filter,
        orderBy: String? = null,
        orderDirection: Query.Direction? = null
    ): List<Finance> {
        return dataStorage.filterDatasets(
            userId = userId,
            filter = filter,
            orderBy = orderBy,
            orderDirection = orderDirection
        )
    }

    suspend fun updateData(
        userId: String,
        oldFinance: Finance,
        newFinance: Finance
    ) {
        dataStorage.updateDataset(
            userId = userId,
            oldFinance = oldFinance,
            newFinance = newFinance
        )
    }

    suspend fun removeData(userId: String, finance: Finance) {
        dataStorage.removeDataset(userId, finance)
    }

    suspend fun addAdjustment(
        userId: String,
        financeId: String,
        adjustment: Adjustment
    ) {
        dataStorage.addAdjustmentDataset(
            userId = userId,
            datasetId = financeId,
            adjustment = adjustment
        )
    }

    suspend fun updateAdjustment(
        userId: String,
        financeId: String,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    ) {
        dataStorage.updateAdjustmentDataset(
            userId = userId,
            datasetId = financeId,
            oldAdjustment = oldAdjustment,
            newAdjustment = newAdjustment
        )
    }

    suspend fun removeAdjustment(
        userId: String,
        financeId: String,
        adjustment: Adjustment
    ) {
        dataStorage.removeAdjustmentDataset(
            userId = userId,
            datasetId = financeId,
            adjustment = adjustment
        )
    }
}