package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp


data class Dataset(
    val id: String = "",
    val dataType: DataType,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val labelIcon: Int,
    val repay: List<Repay> = emptyList()
) {
    fun wasRepaid(): Boolean {
        return repay.sumOf { it.amount } == amount
    }
}
