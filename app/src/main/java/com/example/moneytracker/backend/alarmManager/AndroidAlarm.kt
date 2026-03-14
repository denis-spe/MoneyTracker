// Glory be to the name of LORD our GOD
package com.example.moneytracker.backend.alarmManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAlarm @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidAlarmManager {

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AndroidAlarmReceiver::class.java).apply {
        action = "com.example.moneytracker.ALARM_ACTION"
    }

    /**
     * Schedules an alarm for the given [alarmItem].
     */
    override fun schedule(alarmItem: AlarmItem) {
        // 1. Create a fresh intent or use the existing one, but add extras FIRST
        val alarmIntent = Intent(context, AndroidAlarmReceiver::class.java).apply {
            action = "com.example.moneytracker.ALARM_ACTION"
            putExtra("datasetId", alarmItem.datasetId)
            putExtra("userId", alarmItem.userId)
            putExtra("routine", alarmItem.routineData.routine.name)
        }

        // 2. Pass the updated intent to getBroadcast
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.datasetId.hashCode(), // Use a unique request code per dataset
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = alarmItem.triggerMillis()


        // 3. Schedule the alarm


        try {
            // Check if we can schedule exact alarms (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    // Fallback to inexact alarm if exact alarm permission is denied
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                // For older Android versions, use setExactAndAllowWhileIdle directly
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Handle SecurityException by falling back to inexact alarm
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    /**
     * Cancels the alarm for the given [alarmItem].
     */
    override fun cancel(alarmItem: AlarmItem) {
        // Build the same intent and pendingIntent used in schedule(...)
        val alarmIntent = Intent(context, AndroidAlarmReceiver::class.java).apply {
            action = "com.example.moneytracker.ALARM_ACTION"
            putExtra("datasetId", alarmItem.datasetId)
            putExtra("userId", alarmItem.userId)
            putExtra("routine", alarmItem.routineData.routine.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.datasetId.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel() // also cancel the PendingIntent itself
            Log.d("AndroidAlarm", "Cancelled alarm for dataset=${alarmItem.datasetId}")
        } catch (e: Exception) {
            Log.e("AndroidAlarm", "Failed to cancel alarm", e)
        }
    }

}