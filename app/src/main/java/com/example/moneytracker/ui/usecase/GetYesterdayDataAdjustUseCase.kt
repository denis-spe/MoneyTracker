package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isForYesterday
import javax.inject.Inject

class GetYesterdayDataAdjustUseCase @Inject constructor() {

    operator fun invoke(datasets: List<Dataset>): List<DataAdjust> {
        return coupleDatasetsWithAdjustments(datasets).filter {
            when (it) {
                is DataAdjust.Data -> it.dataset.isForYesterday
                is DataAdjust.Adjust -> it.adjustment.isForYesterday
            }
        }
    }
}