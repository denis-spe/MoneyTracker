// Great is the LORD of host
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.backend.storage.types.SettlementType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

@Keep
data class Settlement(
    val settlementId: String,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val tagIcon: TagIcon,
    val settlementType: SettlementType,
    val paymentMethod: PaymentMethod,
    val userId: String = "",
    val datasetId: String = "",
    val affectCurrentAccount: Boolean = false,
) {
    @Exclude
    var financeEntity: FinanceEntity? = null
}

