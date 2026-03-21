// Bless be the name of LORD of hosts and our LORD JESUS
package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp

data class StatusHistory(
    val status: String,
    val adjustmentAmount: Double,
    val dateTime: Timestamp,
    val deadlineTime: Timestamp
)
