package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.helper.toLocalDateTimeUtc
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetWeeklyDataUseCase @Inject constructor() {

    operator fun invoke(
        financeList: List<Finance>,
        dates: List<LocalDate>
    ): List<DataAdjust> {
        return coupleDatasetsWithAdjustments(financeList).filter { item ->
            when (item) {
                is DataAdjust.Data ->
                    item.finance.createdAt.toLocalDateTimeUtc().date in dates

                is DataAdjust.Adjust ->
                    item.adjustment.dateTime.toLocalDateTimeUtc().date in dates
            }
        }
    }
}