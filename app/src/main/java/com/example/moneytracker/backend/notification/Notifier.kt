package com.example.moneytracker.backend.notification

import android.content.Context
import androidx.core.app.NotificationCompat

interface Notifier {
    fun showNotification(notificationItem: NotificationItem)
    fun buildForegroundNotification(context: Context): android.app.Notification
    val channelId: String
    var builder: NotificationCompat.Builder
}
