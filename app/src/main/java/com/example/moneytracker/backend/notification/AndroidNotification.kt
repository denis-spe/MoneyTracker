package com.example.moneytracker.backend.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.moneytracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNotification @Inject constructor(
    @ApplicationContext private val context: Context
) : Notifier {

    private val channelId = "money_tracker_alarms"

    init {
        createNotificationChannel()
    }

    override fun showNotification(notificationItem: NotificationItem) {
        val title = notificationItem.title
        val message = notificationItem.message
        val bigMassage = notificationItem.bigMessage
        val icon = notificationItem.icon

        Log.d("AndroidNotification", "showNotification called: $title - $message")

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigMassage ?: ""))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        try {
            if (notificationManager.areNotificationsEnabled()) {
                val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                notificationManager.notify(notificationId, builder.build())
            } else {
                Log.w("AndroidNotification", "Notifications are disabled")
            }
        } catch (e: Exception) {
            Log.e("AndroidNotification", "Error showing notification", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH // Increased importance
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("AndroidNotification", "Notification channel created: $channelId")
        }
    }
}
