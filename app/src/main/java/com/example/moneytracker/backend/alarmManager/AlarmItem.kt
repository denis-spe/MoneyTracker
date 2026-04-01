package com.example.moneytracker.backend.alarmManager

data class AlarmItem(
    val datasetId: String,
    val userId: String,
    val triggerMillis: Long,
)
