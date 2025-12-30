package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Dataset(
    @DocumentId val id: String? = null,
    val dataType: DataType,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val labelIcon: Int,
    val repay: List<Repay>? = null
) {
    fun wasRepaid(): Boolean {
        if (repay != null) {
            return repay.sumOf { it.amount } == amount
        }
        return false
    }
}
