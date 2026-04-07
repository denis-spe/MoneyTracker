package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.toLocalDateTimeUtc
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetLenOfActivatesUseCase @Inject constructor() {
    operator fun invoke(datasets: List<Dataset>, date: LocalDate): Int {
        val datasetLen = datasets.filter { it.dateTime.toLocalDateTimeUtc().date == date }.size
        val adjustLen = datasets.flatMap { it.adjustment }
            .filter { it.dateTime.toLocalDateTimeUtc().date == date }.size
        return datasetLen + adjustLen
    }
}
