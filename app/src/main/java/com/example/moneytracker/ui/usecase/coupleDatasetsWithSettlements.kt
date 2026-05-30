package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity

fun coupleDatasetsWithSettlements(financeEntityList: List<FinanceEntity>): List<DataSettlement> {
    val adjust = financeEntityList.flatMap { finance ->
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

    val withdrawals = financeEntityList.flatMap { finance ->
        val withdrawalList = when (finance) {
            is FinanceEntity.Transaction -> finance.withdrawal
            else -> emptyList()
        }
        withdrawalList.map { withdrawal ->
            withdrawal.financeEntity = finance
            DataSettlement.SettlementWithdrawal(withdrawal)
        }
    }

    val data = financeEntityList.map { finance ->
        DataSettlement.SettlementData(finance)
    }

    return adjust + withdrawals + data
}
