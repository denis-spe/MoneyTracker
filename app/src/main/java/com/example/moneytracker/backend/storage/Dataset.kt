package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp

data class Dataset(
    val dataType: DataType,
    val amount: Double,
    val label: String,
    val description: String,
    val dateTime: Timestamp,
    val labelIcon: Int,
)
