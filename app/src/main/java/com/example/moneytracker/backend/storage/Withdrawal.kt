package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

@Keep
data class Withdrawal(
    val withdrawalId: String = "",
    val datasetId: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val label: String = "",
    val description: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val toPaymentMethod: PaymentMethod = PaymentMethod.CASH,
    val fromPaymentMethod: PaymentMethod = PaymentMethod.CREDIT_CARD
) {
    @Exclude
    var financeEntity: FinanceEntity? = null
}
