// Glory be to name of LORD of hosts
package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import javax.inject.Inject

class GetCurrentAmountUseCase @Inject constructor() {
    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
    ): Map<String, Double> {

        fun calculateBalance(method: PaymentMethod): Double {
            var incoming = 0.0
            var outgoing = 0.0

            financeEntityList.forEach { entity ->
                // 1. Process the main entity principal if it matches the method
                if (entity.paymentMethod == method) {
                    when (entity) {
                        is FinanceEntity.Liability -> {
                            if (entity.affectCurrentAccount) {
                                if (entity.liabilityType.text == "Debt") {
                                    // Debt: Income only if we actually received the money
                                    incoming += entity.amount
                                } else {
                                    // Lent: Money left our account
                                    outgoing += entity.amount
                                }
                            }
                        }

                        is FinanceEntity.Transaction -> {
                            if (entity.affectCurrentAccount) {
                                if (entity.financeType.text == "Earnings") incoming += entity.amount
                                else outgoing += entity.amount
                            }
                        }

                        is FinanceEntity.Goal -> {
                            // Goals don't have a principal amount that leaves the account immediately
                        }
                    }
                }

                // 2. Process settlements within the entity
                val settlements = when (entity) {
                    is FinanceEntity.Goal -> entity.settlement
                    is FinanceEntity.Liability -> entity.settlement
                    is FinanceEntity.Transaction -> emptyList()
                }

                settlements.forEach { settlement ->
                    if (settlement.paymentMethod == method && settlement.affectCurrentAccount) {
                        when (settlement.settlementType.text) {
                            "Refund" -> incoming += settlement.amount
                            "Payback",
                            "Withdrawal" -> outgoing += settlement.amount
                        }
                    }
                }

                // Process withdrawals within the entity
                val withdrawals = when (entity) {
                    is FinanceEntity.Transaction -> entity.withdrawal
                    else -> emptyList()
                }

                withdrawals.forEach { withdrawal ->
                    if (!withdrawal.affectCurrentAccount) return@forEach
                    if (withdrawal.fromPaymentMethod == method) {
                        outgoing += withdrawal.amount
                    }
                    if (withdrawal.toPaymentMethod == method) {
                        incoming += withdrawal.amount
                    }
                }
            }
            return incoming - outgoing
        }

        return mapOf(
            "Card" to calculateBalance(PaymentMethod.CREDIT_CARD),
            "Cash" to calculateBalance(PaymentMethod.CASH)
        )
    }
}