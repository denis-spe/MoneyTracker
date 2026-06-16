package com.example.moneytracker.ui.usecase

import android.util.Log
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.isCreatedAtEqualTo
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

class GetYesterdayStatsUseCase @Inject constructor() {
    operator fun invoke(financeEntityList: List<FinanceEntity>): YesterdayStats {
        return try {
            val yesterday = LocalDateTime.now().date.minus(1, DateTimeUnit.DAY)

            var earnings = 0.0
            var expenses = 0.0
            var debts = 0.0
            var lent = 0.0
            var savings = 0.0
            var payback = 0.0
            var refund = 0.0

            for (finance in financeEntityList) {
                if (finance.isCreatedAtEqualTo(yesterday)) {
                    when (finance) {
                        is FinanceEntity.Transaction -> {
                            when (finance.transactionType) {
                                TransactionType.EARNINGS -> earnings += finance.amount
                                TransactionType.EXPENSES -> expenses += finance.amount
                                TransactionType.SAVINGS -> savings += finance.amount
                            }
                        }

                        is FinanceEntity.Liability -> {
                            when (finance.liabilityType) {
                                LiabilityType.DEBT -> {
                                    if (finance.affectCurrentAccount) {
                                        debts += finance.amount
                                    }
                                }
                                LiabilityType.LOAN -> lent += finance.amount
                            }
                        }

                        is FinanceEntity.Goal -> {}
                    }
                }

                val settlements = when (finance) {
                    is FinanceEntity.Goal -> finance.settlement
                    is FinanceEntity.Liability -> finance.settlement
                    is FinanceEntity.Transaction -> emptyList()
                }

                for (settlement in settlements) {
                    if (settlement.isCreatedAtEqualTo(yesterday)) {
                        when (settlement.settlementType.text) {
                            "Refund" -> refund += settlement.amount
                            "Payback" -> payback += settlement.amount
                        }
                    }
                }
            }

            val reminder = (earnings + debts + refund) - (expenses + lent + payback + savings)

            YesterdayStats(
                earnings = earnings,
                expenses = expenses,
                debts = debts,
                lent = lent,
                payback = payback,
                refund = refund,
                savings = savings,
                reminder = reminder
            )
        } catch (e: Exception) {
            Log.e("GetYesterdayStats", "Error calculating stats", e)
            YesterdayStats()
        }
    }
}
