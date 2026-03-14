package com.example.moneytracker.backend.notification

interface Notifier {
    fun showNotification(notificationItem: NotificationItem)
}
