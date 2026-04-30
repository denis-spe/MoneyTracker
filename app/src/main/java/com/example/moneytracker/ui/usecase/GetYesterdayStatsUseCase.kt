package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.backend.storage.LiabilityType
import com.example.moneytracker.backend.storage.TransactionType
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import javax.inject.Inject

class GetYesterdayStatsUseCase @Inject constructor() {
    operator fun invoke(financeList: List<Finance>): YesterdayStats {
        val earnings =
            financeList.filter { it is Finance.Transaction && it.transactionType == TransactionType.EARNINGS }
                .sumOf { it.amount }
        val expenses =
            financeList.filter { it is Finance.Transaction && it.transactionType == TransactionType.EXPENSES }
                .sumOf { it.amount }
        val debts =
            financeList.filter { it is Finance.Liability && it.liabilityType == LiabilityType.DEBT }
                .sumOf { it.amount }
        val lent =
            financeList.filter { it is Finance.Liability && it.liabilityType == LiabilityType.LOAN }
                .sumOf { it.amount }
        val savings =
            financeList.filter { it is Finance.Transaction && it.transactionType == TransactionType.SAVINGS }
                .sumOf { it.amount }
        val attained = financeList.filterIsInstance<Finance.Goal>()
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