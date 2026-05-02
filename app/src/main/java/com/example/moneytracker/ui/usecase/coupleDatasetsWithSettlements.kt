package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity

internal fun coupleDatasetsWithSettlements(financeEntityList: List<FinanceEntity>): List<DataSettlement> {
    val adjust = financeEntityList.map { finance ->
        val settlements = when (finance) {
            is FinanceEntity.Goal -> finance.settlement
            is FinanceEntity.Liability -> finance.settlement
            is FinanceEntity.Transaction -> emptyList()
        }
        settlements.map { settlement ->
            settlement.financeEntity = finance
            DataSettlement.SettlementAdjust(settlement)
        }
    }

    val data = financeEntityList.map { finance ->
        DataSettlement.SettlementData(finance)
    }

    return adjust.flatten() + data
}