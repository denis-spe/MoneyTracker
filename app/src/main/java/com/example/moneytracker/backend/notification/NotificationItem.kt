package com.example.moneytracker.backend.notification

data class NotificationItem(
    val title: String,
    val message: String,
    val bigMessage: String? = null,
    val icon: Int,
    val largeIcon: Int
)
