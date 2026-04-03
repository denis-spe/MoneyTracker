package com.example.moneytracker.backend.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.moneytracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNotification @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override val channelId: String = "money_tracker_alarms"
    override var builder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)

    init {
        createNotificationChannel()
    }

    override fun showNotification(notificationItem: NotificationItem) {
        val title = notificationItem.title
        val message = notificationItem.message
        val bigMassage = notificationItem.bigMessage
        val icon = notificationItem.icon
        val largeIcon = try {
            BitmapFactory.decodeResource(context.resources, notificationItem.largeIcon)
        } catch (e: Exception) {
            null
        }

        Log.d("AndroidNotification", "showNotification called: $title - $message")

        builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigMassage ?: ""))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        try {
            // Check if notifications are enabled
            if (notificationManager.areNotificationsEnabled()) {
                // Check runtime permission for Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                        notificationManager.notify(notificationId, builder.build())
                        Log.d("AndroidNotification", "Notification shown successfully")
                    } else {
                        Log.w("AndroidNotification", "POST_NOTIFICATIONS permission not granted")
                    }
                } else {
                    // For Android < 13, just show the notification
                    val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                    notificationManager.notify(notificationId, builder.build())
                    Log.d("AndroidNotification", "Notification shown successfully")
                }
            } else {
                Log.w("AndroidNotification", "Notifications are disabled")
            }
        } catch (e: SecurityException) {
            Log.e(
                "AndroidNotification",
                "SecurityException: POST_NOTIFICATIONS permission denied",
                e
            )
        } catch (e: Exception) {
            Log.e("AndroidNotification", "Error showing notification", e)
        }
    }

    // In your Notifier class (usually injected via Hilt)
    override fun buildForegroundNotification(context: Context): android.app.Notification {
        val channelId = "money_tracker_updates"
        val channelName = "Routine Updates"

        // 1. Create the Notification Channel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW // Low priority so it doesn't beep every time
                )
                manager.createNotificationChannel(channel)
            }
        }

        // 2. Build the notification
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Money Tracker")
            .setContentText("Syncing routine data...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app icon
            .setOngoing(true) // Prevents user from swiping it away while working
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
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
