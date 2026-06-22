// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// LORD your GOD with all your soul and with all your might and with all your strength
// and love your neighbor as your self.
package com.example.moneytracker.backend.storage

import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.isAmountEqualToSettleAmount
import com.google.firebase.Timestamp


sealed class DataSettlement {
    val id: String
        get() = when (this) {
            is SettlementData -> financeEntity.id
            is SettlementAdjust -> settlement.settlementId
            is SettlementWithdrawal -> withdrawal.withdrawalId.ifEmpty { "${withdrawal.datasetId}_${withdrawal.createdAt.seconds}" }
        }

    val label: String
        get() = when (this) {
            is SettlementData -> financeEntity.label
            is SettlementAdjust -> settlement.label
            is SettlementWithdrawal -> withdrawal.label
        }

    val text: String
        get() = when (this) {
            is SettlementData -> financeEntity.categoryText
            is SettlementAdjust -> settlement.settlementType.text
            is SettlementWithdrawal -> SettlementType.WITHDRAWAL.text
        }

    val description: String
        get() = when (this) {
            is SettlementData -> financeEntity.description
            is SettlementAdjust -> settlement.description
            is SettlementWithdrawal -> withdrawal.description
        }

    val amount: Double
        get() = when (this) {
            is SettlementData -> financeEntity.amount
            is SettlementAdjust -> settlement.amount
            is SettlementWithdrawal -> withdrawal.amount
        }

    val colorRes: Int
        get() = when (this) {
            is SettlementData -> financeEntity.colorRes
            is SettlementAdjust -> settlement.settlementType.color
            is SettlementWithdrawal -> SettlementType.WITHDRAWAL.color
        }

    val tagIcon: TagIcon
        get() = when (this) {
            is SettlementData -> financeEntity.tagIcon
            is SettlementAdjust -> settlement.tagIcon
            is SettlementWithdrawal -> withdrawal.financeEntity?.tagIcon
                ?: TagIcon(name = "Withdrawal", icon = R.drawable.initial)
        }

    val affectCurrentAccount: Boolean
        get() = when (this) {
            is SettlementData -> when (financeEntity) {
                is FinanceEntity.Transaction -> financeEntity.affectCurrentAccount
                is FinanceEntity.Goal -> false
                is FinanceEntity.Liability -> financeEntity.affectCurrentAccount
            }

            is SettlementAdjust -> settlement.affectCurrentAccount
            is SettlementWithdrawal -> withdrawal.affectCurrentAccount
        }


    val icon: Int
        get() = when (this) {
            is SettlementData -> when (val f = financeEntity) {
                is FinanceEntity.Transaction -> when (f.transactionType) {
                    TransactionType.EARNINGS -> R.drawable.filled_earnings
                    TransactionType.EXPENSES -> R.drawable.filled_expenditure
                    TransactionType.SAVINGS -> R.drawable.filled_savings
                }

                is FinanceEntity.Goal -> R.drawable.filled_goal
                is FinanceEntity.Liability -> when (f.liabilityType) {
                    LiabilityType.DEBT -> R.drawable.filled_debt
                    LiabilityType.LOAN -> R.drawable.filled_lent
                }
            }

            is SettlementAdjust -> settlement.settlementType.icon
            is SettlementWithdrawal -> SettlementType.WITHDRAWAL.icon
        }

    val outlineIcon: Int
        get() = when (this) {
            is SettlementData -> when (val f = financeEntity) {
                is FinanceEntity.Transaction -> when (f.transactionType) {
                    TransactionType.EARNINGS -> R.drawable.outline_earnings
                    TransactionType.EXPENSES -> R.drawable.outline_expenditure
                    TransactionType.SAVINGS -> R.drawable.outline_savings
                }

                is FinanceEntity.Goal -> R.drawable.outlined_goal
                is FinanceEntity.Liability -> when (f.liabilityType) {
                    LiabilityType.DEBT -> R.drawable.outline_debt
                    LiabilityType.LOAN -> R.drawable.outline_lent
                }
            }

            is SettlementAdjust -> settlement.settlementType.outlineIcon
            is SettlementWithdrawal -> SettlementType.WITHDRAWAL.icon
        }

    val createdAt: Timestamp
        get() = when (this) {
            is SettlementData -> financeEntity.createdAt
            is SettlementAdjust -> settlement.dateTime
            is SettlementWithdrawal -> withdrawal.createdAt
        }

    val financeEntityType: String
        get() = when (this) {
            is SettlementAdjust -> {
                when (settlement.financeEntity) {
                    is FinanceEntity.Transaction -> "TRANSACTION"
                    is FinanceEntity.Goal -> "ATTAINMENT"
                    is FinanceEntity.Liability -> "LIABILITY"
                    null -> "TRANSACTION"
                }
            }

            is SettlementWithdrawal -> {
                when (withdrawal.financeEntity) {
                    is FinanceEntity.Transaction -> "TRANSACTION"
                    is FinanceEntity.Goal -> "GOAL"
                    is FinanceEntity.Liability -> "LIABILITY"
                    null -> "TRANSACTION"
                }
            }

            is SettlementData -> {
                when (financeEntity) {
                    is FinanceEntity.Transaction -> "TRANSACTION"
                    is FinanceEntity.Goal -> "GOAL"
                    is FinanceEntity.Liability -> "LIABILITY"
                }
            }
        }

    val paymentMethod: PaymentMethod
        get() = when (this) {
            is SettlementData -> financeEntity.paymentMethod
            is SettlementAdjust -> settlement.paymentMethod
            is SettlementWithdrawal -> withdrawal.toPaymentMethod
        }

    val isAmountEqualWithAdjustAmount: Boolean?
        get() = when (this) {
            is SettlementData -> {
                financeEntity.isAmountEqualToSettleAmount()
            }

            is SettlementAdjust -> {
                settlement.financeEntity?.isAmountEqualToSettleAmount()
            }

            is SettlementWithdrawal -> {
                withdrawal.financeEntity?.isAmountEqualToSettleAmount()
            }

        }

    data class SettlementData(val financeEntity: FinanceEntity) : DataSettlement()
    data class SettlementAdjust(val settlement: Settlement) : DataSettlement()
    data class SettlementWithdrawal(val withdrawal: Withdrawal) : DataSettlement()
}
