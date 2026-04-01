// Bless be the name of LORD of hosts and our LORD JESUS
package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp

data class StatusHistory(
    val status: String,
    val totalAdjustmentAmount: Double,     // Total amount of adjustments for the dataset
    val startDateTime: Timestamp,          // When the dataset/routine started
    val deadlineDateTime: Timestamp        // When the deadline is
)
