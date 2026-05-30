// Bless be the name of LORD of hosts and our LORD JESUS
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Achievement(
    val achievementId: String = "",
    val userId: String = "",
    val datasetId: String = "",
    val status: String,
    val totalSettlementAmount: Double,     // Total amount of settlements for the dataset
    val startDateTime: Timestamp,          // When the dataset/routine started
    val deadlineDateTime: Timestamp        // When the deadline is reached
)
