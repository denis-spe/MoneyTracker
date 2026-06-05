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
                // Process the main entity if it matches the payment method
                if (entity.paymentMethod == method) {
                    when (entity.financeType.text) {
                        "Earnings",
                        "Debt" -> {
                            if (entity is FinanceEntity.Liability && !entity.isAmountReceived) {
                                return@forEach
                            }

                            incoming += entity.amount
                        }

                        "Expense",
                        "Lent",
                        "Savings",
                        "Withdrawal" -> outgoing += entity.amount
                    }
                }

                // Process settlements within the entity
                val settlements = when (entity) {
                    is FinanceEntity.Goal -> entity.settlement
                    is FinanceEntity.Liability -> entity.settlement
                    is FinanceEntity.Transaction -> emptyList()
                }

                settlements.forEach { settlement ->
                    if (settlement.paymentMethod == method) {
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