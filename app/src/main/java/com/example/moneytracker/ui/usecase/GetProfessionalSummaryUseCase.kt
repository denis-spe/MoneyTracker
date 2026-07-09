package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.types.SettlementType
import javax.inject.Inject

data class ProfessionalSummary(
    val totalIncome: Double = 0.0,
    val totalOutcome: Double = 0.0,
    val netBalance: Double = 0.0,
    val transactionCount: Int = 0,
    val savings: Double = 0.0,
    val largestTransaction: Double = 0.0
)

class GetProfessionalSummaryUseCase @Inject constructor() {

    operator fun invoke(dataSettlements: List<DataSettlement>): ProfessionalSummary {
        var income = 0.0
        var outcome = 0.0
        var savings = 0.0
        var largest = 0.0

        dataSettlements.forEach { item ->
            val amount = item.amount
            if (amount > largest) largest = amount

            when (item) {
                is DataSettlement.SettlementData -> {
                    when (val finance = item.financeEntity) {
                        is FinanceEntity.Transaction -> {
                            when (finance.transactionType.text) {
                                DataType.EARNINGS.text -> income += amount
                                DataType.EXPENSE.text -> outcome += amount
                                DataType.SAVINGS.text -> {
                                    outcome += amount
                                    savings += amount
                                }
                            }
                        }

                        is FinanceEntity.Liability -> {
                            when (finance.liabilityType.text) {
                                DataType.DEBT.text -> income += amount
                                DataType.LENT.text -> outcome += amount
                            }
                        }

                        is FinanceEntity.Goal -> {
                            // Goal creation doesn't necessarily mean money flow until settlement
                        }
                    }
                }

                is DataSettlement.SettlementAdjust -> {
                    val settlement = item.settlement
                    when (settlement.settlementType) {
                        SettlementType.LENT_REPAY -> income += amount
                        SettlementType.DEBT_REPAY -> outcome += amount
                        else -> {}
                    }
                }

                is DataSettlement.SettlementWithdrawal -> {
                }
            }
        }

        return ProfessionalSummary(
            totalIncome = income,
            totalOutcome = outcome,
            netBalance = income - outcome,
            transactionCount = dataSettlements.size,
            savings = savings,
            largestTransaction = largest
        )
    }
}
