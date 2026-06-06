package com.example.moneytracker.ui.usecase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.components.charts.DonutChartData
import javax.inject.Inject

class GetTodayChartDonutDataUseCase @Inject constructor() {
    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
        context: Context
    ): List<DonutChartData> {
        val coupledData = coupleDatasetsWithSettlements(financeEntityList).filter { item ->
            item.isForToday
        }.filterNot {
            when (it) {
                is DataSettlement.SettlementData -> {
                    val entity = it.financeEntity
                    (entity is FinanceEntity.Goal) ||
                            (entity is FinanceEntity.Liability &&
                                    entity.liabilityType == com.example.moneytracker.backend.storage.types.LiabilityType.DEBT &&
                                    !entity.isAmountReceived)
                }
                is DataSettlement.SettlementAdjust -> it.settlement.settlementType == SettlementType.GOAL_ATTAIN
                is DataSettlement.SettlementWithdrawal -> true
            }
        }

        val grouped = coupledData.groupBy { it.text }

        return grouped.values.map { list ->
            val firstItem = list.first()

            val colorResId = firstItem.colorRes
            val title = firstItem.text
            val amount = list.sumOf { it.amount }.toFloat()

            val color = Color(ContextCompat.getColor(context, colorResId))

            DonutChartData(
                amount = amount,
                color = color,
                title = title
            )
        }
    }
}
