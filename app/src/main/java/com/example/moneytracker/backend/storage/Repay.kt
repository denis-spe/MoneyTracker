// Great is the LORD of host
package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp

data class Repay(
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val repayIcon: Int,
)
