package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isCreatedAtEqualTo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

class GetYesterdayDatasetsUseCase @Inject constructor() {
    operator fun invoke(datasets: List<Dataset>): List<Dataset> {
        val yesterday = LocalDateTime.now()
            .date
            .minus(1, DateTimeUnit.DAY)

        return datasets.filter { it.isCreatedAtEqualTo(yesterday) }
    }
}
