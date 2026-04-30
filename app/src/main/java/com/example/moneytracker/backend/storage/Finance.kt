// Glory to the LORD our GOD
package com.example.moneytracker.backend.storage

import com.example.moneytracker.R
import com.google.firebase.Timestamp

enum class TransactionType {
    EARNINGS,
    SAVINGS,
    EXPENSES
}

enum class LiabilityType {
    DEBT,
    LOAN
}

sealed class Finance {
    abstract val id: String
    abstract val amount: Double
    abstract val label: String
    abstract val description: String
    abstract val createdAt: Timestamp
    abstract val tagIcon: TagIcon
    abstract val paymentMethod: PaymentMethod

    val categoryText: String
        get() = when (this) {
            is Transaction -> when (transactionType) {
                TransactionType.EARNINGS -> "Earnings"
                TransactionType.SAVINGS -> "Savings"
                TransactionType.EXPENSES -> "Expense"
            }

            is Goal -> "Goal"
            is Liability -> when (liabilityType) {
                LiabilityType.DEBT -> "Debt"
                LiabilityType.LOAN -> "Lent"
            }
        }

    val colorRes: Int
        get() = when (this) {
            is Transaction -> when (transactionType) {
                TransactionType.EARNINGS -> R.color.Earnings
                TransactionType.SAVINGS -> R.color.Savings
                TransactionType.EXPENSES -> R.color.Expense
            }

            is Goal -> R.color.Goal
            is Liability -> when (liabilityType) {
                LiabilityType.DEBT -> R.color.Debt
                LiabilityType.LOAN -> R.color.Lent
            }
        }

    data class Transaction(
        override val id: String = "",
        val transactionType: TransactionType = TransactionType.EARNINGS,
        override val amount: Double = 0.0,
        override val label: String = "",
        override val description: String = "",
        override val createdAt: Timestamp = Timestamp.now(),
        override val tagIcon: TagIcon = TagIcon(),
        override val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    ) : Finance()

    data class Goal(
        override val id: String = "",
        override val amount: Double = 0.0,
        override val label: String = "",
        override val description: String = "",
        override val createdAt: Timestamp = Timestamp.now(),
        override val tagIcon: TagIcon = TagIcon(),
        override val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val adjustment: List<Adjustment> = emptyList(),
        val statusHistory: List<StatusHistory> = emptyList(),
        val routine: RoutineData = RoutineData()
    ) : Finance()

    data class Liability(
        override val id: String = "",
        val liabilityType: LiabilityType = LiabilityType.LOAN,
        override val amount: Double = 0.0,
        override val label: String = "",
        override val description: String = "",
        override val createdAt: Timestamp = Timestamp.now(),
        override val tagIcon: TagIcon = TagIcon(),
        override val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val adjustment: List<Adjustment> = emptyList(),
    ) : Finance()
}
