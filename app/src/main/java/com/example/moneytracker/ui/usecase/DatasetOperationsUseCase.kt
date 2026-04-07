package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import javax.inject.Inject

class DatasetOperationsUseCase @Inject constructor(
    private val dataStorage: DataStorage
) {

    suspend fun addData(userId: String, dataset: Dataset): String {
        return dataStorage.addData(userId, dataset)
    }

    suspend fun updateData(
        userId: String,
        oldDataset: Dataset,
        newDataset: Dataset
    ) {
        dataStorage.updateDataset(
            userId = userId,
            oldDataset = oldDataset,
            newDataset = newDataset
        )
    }

    suspend fun removeData(userId: String, dataset: Dataset) {
        dataStorage.removeDataset(userId, dataset)
    }

    suspend fun addAdjustment(
        userId: String,
        datasetId: String,
        adjustment: Adjustment
    ) {
        dataStorage.addAdjustmentDataset(
            userId = userId,
            datasetId = datasetId,
            adjustment = adjustment
        )
    }

    suspend fun updateAdjustment(
        userId: String,
        datasetId: String,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    ) {
        dataStorage.updateAdjustmentDataset(
            userId = userId,
            datasetId = datasetId,
            oldAdjustment = oldAdjustment,
            newAdjustment = newAdjustment
        )
    }

    suspend fun removeAdjustment(
        userId: String,
        datasetId: String,
        adjustment: Adjustment
    ) {
        dataStorage.removeAdjustmentDataset(
            userId = userId,
            datasetId = datasetId,
            adjustment = adjustment
        )
    }
}