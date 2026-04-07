package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.Info

data class HomeData(
    val datasets: List<Dataset> = emptyList(),
    val info: Info = Info(),
    val datasetState: DatasetState = DatasetState.Loading,
    val error: String? = null
)
