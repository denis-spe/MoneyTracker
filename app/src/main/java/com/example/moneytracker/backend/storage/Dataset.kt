package com.example.moneytracker.backend.storage

import java.util.Date

data class Dataset(
    val userId: String,
    val id: String? = null,
    val dataType: DataType,
    val amount: Double,
    val label: String,
    val description: String? = null,
    val dateTime: Date = Date()
)
