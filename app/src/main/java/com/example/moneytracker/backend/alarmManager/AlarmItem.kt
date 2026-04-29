package com.example.moneytracker.backend.alarmManager

import java.time.LocalDateTime

data class AlarmItem(
    val alarmTime: LocalDateTime,
    val message: String
)
