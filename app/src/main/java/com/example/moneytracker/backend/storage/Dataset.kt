// Glory to the LORD our GOD
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.google.firebase.Timestamp


@Keep
data class Dataset(
    val id: String = "",
    val dataType: DataType = DataType.EARNINGS,
    val amount: Double = 0.0,
    val label: String = "",
    val description: String = "",
    val dateTime: Timestamp = Timestamp.now(),
    val deadlineDateTime: Timestamp = Timestamp.now(),
    val labelIcon: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val adjustmentStatus: AdjustmentStatus = AdjustmentStatus.PENDING,
    val adjustment: List<Adjustment> = emptyList()
)
