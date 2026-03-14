// Bless be the name of LORD of hosts
package com.example.moneytracker.backend.alarmManager

/**
 * Interface for the Android alarm manager.
 */
interface AndroidAlarmManager {
    /**
     * Schedules an alarm for the given [alarmItem].
     */
    fun schedule(alarmItem: AlarmItem)

    /**
     * Cancels the alarm for the given [alarmItem].
     */
    fun cancel(alarmItem: AlarmItem)
}