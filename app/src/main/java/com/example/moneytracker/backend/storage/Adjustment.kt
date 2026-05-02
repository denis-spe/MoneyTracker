// Great is the LORD of host
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

@Keep
data class Adjustment(
    val adjustmentId: String,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val tagIcon: TagIcon,
    val adjustmentType: AdjustmentType,
    val paymentMethod: PaymentMethod
) {
    @Exclude
    var financeEntity: FinanceEntity? = null
}

