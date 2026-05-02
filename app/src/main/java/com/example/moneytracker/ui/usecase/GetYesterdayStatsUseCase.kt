package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import javax.inject.Inject

class GetYesterdayStatsUseCase @Inject constructor() {
    operator fun invoke(financeEntityList: List<FinanceEntity>): YesterdayStats {
        val earnings =
            financeEntityList.filter { it is FinanceEntity.Transaction && it.transactionType == TransactionType.EARNINGS }
                .sumOf { it.amount }
        val expenses =
            financeEntityList.filter { it is FinanceEntity.Transaction && it.transactionType == TransactionType.EXPENSES }
                .sumOf { it.amount }
        val debts =
            financeEntityList.filter { it is FinanceEntity.Liability && it.liabilityType == LiabilityType.DEBT }
                .sumOf { it.amount }
        val lent =
            financeEntityList.filter { it is FinanceEntity.Liability && it.liabilityType == LiabilityType.LOAN }
                .sumOf { it.amount }
        val savings =
            financeEntityList.filter { it is FinanceEntity.Transaction && it.transactionType == TransactionType.SAVINGS }
                .sumOf { it.amount }
        val attained = financeEntityList.filterIsInstance<FinanceEntity.Goal>()
            .flatMap { it.adjustment }
            .sumOf { it.amount }

        val reminder = (earnings - expenses) - (debts + lent + savings + attained)

        return YesterdayStats(
            earnings = earnings,
            expenses = expenses,
            debts = debts,
            lent = lent,
            savings = savings,
            attained = attained,
            reminder = reminder
        )
    }
}