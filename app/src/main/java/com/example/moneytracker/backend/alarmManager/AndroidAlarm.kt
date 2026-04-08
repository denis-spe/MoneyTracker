// Glory be to the name of LORD our GOD
package com.example.moneytracker.backend.alarmManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAlarm @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidAlarmManager {

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    companion object {
        const val TAG = "AndroidAlarm"
    }

    private fun buildIntent(alarmItem: AlarmItem) =
        Intent(context, AndroidAlarmReceiver::class.java).apply {
            action = "com.example.moneytracker.ALARM_ACTION"
            data = "timer:${alarmItem.datasetId}".toUri() // use in both schedule and cancel
            putExtra("datasetId", alarmItem.datasetId)
            putExtra("userId", alarmItem.userId)
            putExtra("triggerMillis", alarmItem.triggerMillis)
        }

    override fun schedule(alarmItem: AlarmItem) {
        val intent = buildIntent(alarmItem)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.datasetId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = alarmItem.triggerMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    override fun cancel(alarmItem: AlarmItem) {
        val intent = buildIntent(alarmItem)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.datasetId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

}