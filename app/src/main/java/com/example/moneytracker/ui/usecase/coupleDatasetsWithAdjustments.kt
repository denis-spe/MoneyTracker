package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Finance

internal fun coupleDatasetsWithAdjustments(financeList: List<Finance>): List<DataAdjust> {
    val adjust = financeList.map { finance ->
        val adjustments = when (finance) {
            is Finance.Goal -> finance.adjustment
            is Finance.Liability -> finance.adjustment
            is Finance.Transaction -> emptyList()
        }
        adjustments.map { adjustment ->
            adjustment.finance = finance
            DataAdjust.Adjust(adjustment)
        }
    }

    val data = financeList.map { finance ->
        DataAdjust.Data(finance)
    }

    return adjust.flatten() + data
}