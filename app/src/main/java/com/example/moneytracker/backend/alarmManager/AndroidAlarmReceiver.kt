// Bless be the name of the LORD of hosts
package com.example.moneytracker.backend.alarmManager


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.EntryPointAccessors

class AndroidAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val userId = intent.getStringExtra("userId")
        val datasetId = intent.getStringExtra("datasetId")
        val triggerMillis = intent.getLongExtra("triggerMillis", 0)


        // Use Log.e because Huawei devices often suppress Log.d by default
        val pendingResult = goAsync()

        val entryPoint = try {
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                AlarmReceiverEntryPoint::class.java
            )
        } catch (e: Exception) {
            Log.e("AndroidAlarmReceiver", "Hilt EntryPoint failed", e)
            pendingResult.finish()
            return
        }

        val work = entryPoint.useWorker()

        try {
            // Case 1: System Event (Boot or Update) or Test Action
            if (action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                action == "com.example.moneytracker.TEST_RESCHEDULE"
            ) {

                Log.d("AndroidAlarmReceiver", "System event detected: $action")
                if (!userId.isNullOrBlank()) {
                    work.rescheduleWork(userId)
                    Log.d("AndroidAlarmReceiver", "Rescheduled all work for user: $userId")
                } else {
                    Log.w("AndroidAlarmReceiver", "System event received but userId is null/blank")
                }
                return
            }

            // Case 2: Your Custom Alarm Triggered
            if (userId.isNullOrBlank() || datasetId.isNullOrBlank()) {
                Log.e(
                    "AndroidAlarmReceiver",
                    "Custom alarm triggered but missing extras: userId=$userId, datasetId=$datasetId"
                )
                return
            }

            Log.d(
                "AndroidAlarmReceiver",
                "Alarm triggered for userId=$userId, datasetId=$datasetId. Starting WorkManager."
            )

            // When triggered from an alarm, we want it to run IMMEDIATELY.
            // By passing an empty RoutineData, getTriggerMillisFrom(System.currentTimeMillis()) 
            // inside UseWorker might still calculate a delay if we are not careful.
            // However, our scheduleWork uses triggerMillis - System.currentTimeMillis().
            // If we want it to be 0, we should ensure triggerMillis equals current time.

            work.scheduleWork(userId, datasetId, triggerMillis, false)



            // ✅ FIX: Don't schedule the next alarm here!
            // Let the ScheduleWorker/completeRoutine update Firestore with the new deadline
            // Then RescheduleWorker or a separate mechanism will reschedule the alarm
            // This prevents the infinite loop where the same trigger time is used again
            Log.d(
                "AndroidAlarmReceiver",
                "Work scheduled. Next alarm will be scheduled by completeRoutine"
            )

        } catch (e: Exception) {
            Log.e("AndroidAlarmReceiver", "Error in onReceive", e)
        } finally {
            pendingResult.finish()
        }

    }
}

