package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isForYesterday
import javax.inject.Inject

class GetYesterdayDatasetsUseCase @Inject constructor() {
    operator fun invoke(datasets: List<Dataset>): List<Dataset> {
        return datasets.filter { it.isForYesterday }
    }
}
