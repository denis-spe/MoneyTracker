package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import javax.inject.Inject

class GetYesterdayStatsUseCase @Inject constructor() {
    operator fun invoke(datasets: List<Dataset>): YesterdayStats {
        val earnings = datasets.filter { it.dataType == DataType.EARNINGS }.sumOf { it.amount }
        val expenses = datasets.filter { it.dataType == DataType.EXPENSE }.sumOf { it.amount }
        val debts = datasets.filter { it.dataType == DataType.DEBT }.sumOf { it.amount }
        val lent = datasets.filter { it.dataType == DataType.LENT }.sumOf { it.amount }
        val savings = datasets.filter { it.dataType == DataType.SAVINGS }.sumOf { it.amount }
        val attained = datasets.filter { it.dataType == DataType.GOAL }
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