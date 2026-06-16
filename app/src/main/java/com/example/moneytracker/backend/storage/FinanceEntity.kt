// Glory to the LORD our GOD
package com.example.moneytracker.backend.storage

import com.example.moneytracker.backend.storage.types.FinanceCategory
import com.example.moneytracker.backend.storage.types.GoalType
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.google.firebase.Timestamp

sealed class FinanceEntity {
    abstract val id: String
    abstract val amount: Double
    abstract val label: String
    abstract val description: String
    abstract val createdAt: Timestamp
    abstract val tagIcon: TagIcon
    abstract val paymentMethod: PaymentMethod
    abstract val financeType: FinanceCategory

    val categoryText: String
        get() = financeType.text

    val colorRes: Int
        get() = financeType.color

    data class Transaction(
        override val id: String = "",
        val transactionType: TransactionType = TransactionType.EARNINGS,
        override val amount: Double = 0.0,
        override val label: String = "",
        override val description: String = "",
        override val createdAt: Timestamp = Timestamp.now(),
        override val tagIcon: TagIcon = TagIcon(),
        override val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val withdrawal: List<Withdrawal> = emptyList(),
        val affectCurrentAccount: Boolean = false
    ) : FinanceEntity() {
        override val financeType: FinanceCategory get() = transactionType
    }

    data class Goal(
        override val id: String = "",
        override val amount: Double = 0.0,
        override val label: String = "",
        override val description: String = "",
        override val createdAt: Timestamp = Timestamp.now(),
        override val tagIcon: TagIcon = TagIcon(),
        override val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val settlement: List<Settlement> = emptyList(),
        val achievement: List<Achievement> = emptyList(),
        val routine: RoutineData = RoutineData()
    ) : FinanceEntity() {
        override val financeType: FinanceCategory get() = GoalType
    }

    data class Liability(
        override val id: String = "",
        val liabilityType: LiabilityType = LiabilityType.LOAN,
        override val amount: Double = 0.0,
        override val label: String = "",
        override val description: String = "",
        override val createdAt: Timestamp = Timestamp.now(),
        override val tagIcon: TagIcon = TagIcon(),
        override val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectCurrentAccount: Boolean = false,
        val settlement: List<Settlement> = emptyList(),
    ) : FinanceEntity() {
        override val financeType: FinanceCategory get() = liabilityType
    }
}
