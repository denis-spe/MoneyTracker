package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.types.LiabilityType
import javax.inject.Inject

class GetLiabilityBalanceUseCase @Inject constructor() {
    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
    ): Map<String, Double> {
        val liabilities = financeEntityList.filterIsInstance<FinanceEntity.Liability>()

        val unpaidDebt = liabilities
            .filter { it.liabilityType == LiabilityType.DEBT }
            .sumOf { it.amount - it.settlement.sumOf { s -> s.amount } }

        val unpaidLent = liabilities
            .filter { it.liabilityType == LiabilityType.LOAN }
            .sumOf { it.amount - it.settlement.sumOf { s -> s.amount } }

        return mapOf("Debt" to unpaidDebt, "Lent" to unpaidLent)
    }
}
