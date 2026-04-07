package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isForToday
import javax.inject.Inject

class GetTodayDatasetsUseCase @Inject constructor() {
    operator fun invoke(datasets: List<Dataset>): List<Dataset> {
        return datasets.filter { it.isForToday }
    }
}
