package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.backend.storage.Info

data class HomeData(
    val datasets: List<Finance> = emptyList(),
    val info: Info = Info(),
    val datasetState: DatasetState = DatasetState.Loading,
    val error: String? = null
)
