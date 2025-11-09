package com.example.moneytracker.backend.storage

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Dataset(
    @DocumentId
    val id: String = "",
    val userId: String,
    val dataType: DataType,
    val amount: Double,
    val label: String,
    val category: String? = null,
    val description: String? = null,
    val dateTime: Date = Date()
)
