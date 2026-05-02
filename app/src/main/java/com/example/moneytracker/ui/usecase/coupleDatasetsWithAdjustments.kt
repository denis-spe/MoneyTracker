package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.FinanceEntity

internal fun coupleDatasetsWithAdjustments(financeEntityList: List<FinanceEntity>): List<DataAdjust> {
    val adjust = financeEntityList.map { finance ->
        val adjustments = when (finance) {
            is FinanceEntity.Goal -> finance.adjustment
            is FinanceEntity.Liability -> finance.adjustment
            is FinanceEntity.Transaction -> emptyList()
        }
        adjustments.map { adjustment ->
            adjustment.financeEntity = finance
            DataAdjust.Adjust(adjustment)
        }
    }

    val data = financeEntityList.map { finance ->
        DataAdjust.Data(finance)
    }

    return adjust.flatten() + data
}