package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isForToday
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTodayDatasetsUseCase @Inject constructor() {
    suspend operator fun invoke(datasets: List<Dataset>): List<Dataset> {
        var todayDatasets = datasets
        withContext(kotlinx.coroutines.Dispatchers.Default) {
            todayDatasets = datasets.filter { it.isForToday }
        }
        return todayDatasets
    }
}
