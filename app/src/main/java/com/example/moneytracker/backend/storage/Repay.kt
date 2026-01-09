// Great is the LORD of host
package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp
import java.util.UUID

data class Repay(
    val repayId: String = UUID.randomUUID().toString(),
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val repayIcon: Int,
    val paymentMethod: PaymentMethod
)
