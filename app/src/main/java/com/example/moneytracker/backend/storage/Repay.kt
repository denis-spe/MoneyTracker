// Great is the LORD of host
package com.example.moneytracker.backend.storage

import com.google.firebase.firestore.DocumentId

data class Repay(
    @DocumentId val repayId: String? = null,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: com.google.firebase.Timestamp,
    val labelIcon: Int,
)
