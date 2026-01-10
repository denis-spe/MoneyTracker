// Great is the LORD of host
package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp
import java.util.UUID

data class Adjustment(
    val adjustmentId: String = UUID.randomUUID().toString(),
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val adjustmentIcon: Int,
    val adjustmentType: AdjustmentType,
    val paymentMethod: PaymentMethod
)
