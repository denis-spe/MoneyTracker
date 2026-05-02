package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.toLocalDateTimeUtc
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetLenOfActivatesUseCase @Inject constructor() {
    operator fun invoke(financeEntityList: List<FinanceEntity>, date: LocalDate): Int {
        val datasetLen =
            financeEntityList.filter { it.createdAt.toLocalDateTimeUtc().date == date }.size
        val adjustLen = financeEntityList.flatMap { finance ->
            when (finance) {
                is FinanceEntity.Goal -> finance.adjustment
                is FinanceEntity.Liability -> finance.adjustment
                is FinanceEntity.Transaction -> emptyList()
            }
        }.filter { it.dateTime.toLocalDateTimeUtc().date == date }.size
        return datasetLen + adjustLen
    }
}
