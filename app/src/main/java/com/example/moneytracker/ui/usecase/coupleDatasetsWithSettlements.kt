package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity

fun coupleDatasetsWithSettlements(financeEntityList: List<FinanceEntity>): List<DataSettlement> {
    // Pre-calculate total size to avoid list resizing
    var totalSize = 0
    for (finance in financeEntityList) {
        totalSize += when (finance) {
            is FinanceEntity.Goal -> finance.settlement.size
            is FinanceEntity.Liability -> finance.settlement.size
            is FinanceEntity.Transaction -> finance.withdrawal.size
        }
        totalSize++ // Account for SettlementData entry
    }

    val result = ArrayList<DataSettlement>(totalSize)

    // Single pass: collect settlements
    for (finance in financeEntityList) {
        val settlements = when (finance) {
            is FinanceEntity.Goal -> finance.settlement
            is FinanceEntity.Liability -> finance.settlement
            is FinanceEntity.Transaction -> emptyList()
        }
        for (settlement in settlements) {
            settlement.financeEntity = finance
            result.add(DataSettlement.SettlementAdjust(settlement))
        }
    }

    // Single pass: collect withdrawals
    for (finance in financeEntityList) {
        val withdrawalList = when (finance) {
            is FinanceEntity.Transaction -> finance.withdrawal
            else -> emptyList()
        }
        for (withdrawal in withdrawalList) {
            withdrawal.financeEntity = finance
            result.add(DataSettlement.SettlementWithdrawal(withdrawal))
        }
    }

    // Single pass: add finance data
    for (finance in financeEntityList) {
        result.add(DataSettlement.SettlementData(finance))
    }

    return result
}
