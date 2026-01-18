package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp


data class Dataset(
    val id: String = "",
    val dataType: DataType,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val deadlineDateTime: Timestamp,
    val labelIcon: Int,
    val paymentMethod: PaymentMethod,
    val adjustmentStatus: AdjustmentStatus,
    val adjustment: List<Adjustment> = emptyList()
) {
    fun isAmountEqualToAdjustAmount(): Boolean {
        return adjustment.sumOf { it.amount } == amount
    }

    val remainingAmount: Double
        get() = amount - adjustment.sumOf { it.amount }

    val isOverdue: Boolean
        get() = deadlineDateTime.toDate().before(Timestamp.now().toDate())

    val status = when {
        isOverdue -> AdjustmentStatus.FAILED
        isAmountEqualToAdjustAmount() -> AdjustmentStatus.COMPLETED
        else -> AdjustmentStatus.PENDING
    }
}
