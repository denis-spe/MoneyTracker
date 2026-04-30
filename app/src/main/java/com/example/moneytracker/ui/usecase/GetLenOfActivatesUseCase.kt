package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.helper.toLocalDateTimeUtc
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetLenOfActivatesUseCase @Inject constructor() {
    operator fun invoke(financeList: List<Finance>, date: LocalDate): Int {
        val datasetLen = financeList.filter { it.createdAt.toLocalDateTimeUtc().date == date }.size
        val adjustLen = financeList.flatMap { finance ->
            when (finance) {
                is Finance.Goal -> finance.adjustment
                is Finance.Liability -> finance.adjustment
                is Finance.Transaction -> emptyList()
            }
        }.filter { it.dateTime.toLocalDateTimeUtc().date == date }.size
        return datasetLen + adjustLen
    }
}
