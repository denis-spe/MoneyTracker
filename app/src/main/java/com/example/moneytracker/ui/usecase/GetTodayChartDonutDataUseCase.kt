package com.example.moneytracker.ui.usecase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.SettlementType
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.components.charts.DonutChartData
import javax.inject.Inject

class GetTodayChartDonutDataUseCase @Inject constructor() {
    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
        context: Context
    ): List<DonutChartData> {
        val coupledData = coupleDatasetsWithSettlements(financeEntityList).filter { item ->
            when (item) {
                is DataSettlement.SettlementData -> item.financeEntity.isForToday
                is DataSettlement.SettlementAdjust -> item.settlement.isForToday
            }
        }.filterNot {
            when (it) {
                is DataSettlement.SettlementData -> it.financeEntity is FinanceEntity.Goal
                is DataSettlement.SettlementAdjust -> it.settlement.settlementType == SettlementType.GOAL_ATTAIN
            }
        }

        val grouped = coupledData.groupBy { item ->
            when (item) {
                is DataSettlement.SettlementData -> item.financeEntity.categoryText
                is DataSettlement.SettlementAdjust -> item.settlement.settlementType.text
            }
        }

        return grouped.values.map { list ->
            val firstItem = list.first()

            val colorResId = when (firstItem) {
                is DataSettlement.SettlementData -> firstItem.financeEntity.colorRes
                is DataSettlement.SettlementAdjust -> firstItem.settlement.settlementType.color
            }

            val title = when (firstItem) {
                is DataSettlement.SettlementData -> firstItem.financeEntity.categoryText
                is DataSettlement.SettlementAdjust -> firstItem.settlement.settlementType.text
            }

            val amount = list.sumOf {
                when (it) {
                    is DataSettlement.SettlementData -> it.financeEntity.amount
                    is DataSettlement.SettlementAdjust -> it.settlement.amount
                }
            }.toFloat()

            val color = Color(ContextCompat.getColor(context, colorResId))

            DonutChartData(
                amount = amount,
                color = color,
                title = title
            )
        }
    }
}