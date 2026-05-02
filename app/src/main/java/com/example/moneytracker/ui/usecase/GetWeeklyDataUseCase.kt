package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.toLocalDateTimeUtc
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetWeeklyDataUseCase @Inject constructor() {

    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
        dates: List<LocalDate>
    ): List<DataAdjust> {
        return coupleDatasetsWithAdjustments(financeEntityList).filter { item ->
            when (item) {
                is DataAdjust.Data ->
                    item.financeEntity.createdAt.toLocalDateTimeUtc().date in dates

                is DataAdjust.Adjust ->
                    item.adjustment.dateTime.toLocalDateTimeUtc().date in dates
            }
        }
    }
}