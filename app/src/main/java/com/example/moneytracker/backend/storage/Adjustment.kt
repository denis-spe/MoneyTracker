// Great is the LORD of host
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Adjustment(
    val adjustmentId: String,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val adjustmentIcon: Int,
    val adjustmentType: AdjustmentType,
    val paymentMethod: PaymentMethod
)
