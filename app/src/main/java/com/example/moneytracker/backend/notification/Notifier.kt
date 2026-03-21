package com.example.moneytracker.backend.notification

import androidx.core.app.NotificationCompat

interface Notifier {
    fun showNotification(notificationItem: NotificationItem)
    val channelId: String
    var builder: NotificationCompat.Builder
}
