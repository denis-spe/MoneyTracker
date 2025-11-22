package com.example.moneytracker.backend.storage

import java.util.Date

data class Dataset(
    val dataType: DataType,
    val amount: Double,
    val label: String,
    val category: String? = null,
    val description: String? = null,
    val dateTime: Date = Date()
)
