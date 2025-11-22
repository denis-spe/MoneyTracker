package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp

fun Map<*, *>.toDataset(): Dataset {
    // 2. Get the field as a Timestamp first
    val timestamp = this["dateTime"] as Timestamp

    return Dataset(
        amount = this["amount"] as Double,
        dataType = DataType.valueOf(this["dataType"] as String),
        label = this["label"] as String,
        category = this["category"] as String?,
        description = this["description"] as String?,
        dateTime = timestamp.toDate()
    )
}