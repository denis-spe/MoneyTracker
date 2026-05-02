package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.toLocalDateTimeUtc
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetWeeklyDataUseCase @Inject constructor() {

    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
        dates: List<LocalDate>
    ): List<DataSettlement> {
        return coupleDatasetsWithSettlements(financeEntityList).filter { item ->
            when (item) {
                is DataSettlement.SettlementData ->
                    item.financeEntity.createdAt.toLocalDateTimeUtc().date in dates

                is DataSettlement.SettlementAdjust ->
                    item.settlement.dateTime.toLocalDateTimeUtc().date in dates
            }
        }
    }
}