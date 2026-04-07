package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset

internal fun coupleDatasetsWithAdjustments(datasets: List<Dataset>): List<DataAdjust> {
    val adjust = datasets.map { dataset ->
        dataset.adjustment.map { adjustment ->
            adjustment.dataset = dataset
            DataAdjust.Adjust(adjustment)
        }
    }

    val data = datasets.map { dataset ->
        DataAdjust.Data(dataset)
    }

    return adjust.flatten() + data
}