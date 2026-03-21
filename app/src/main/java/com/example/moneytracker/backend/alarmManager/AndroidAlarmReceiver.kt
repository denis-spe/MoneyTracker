// Bless be the name of the LORD of hosts
package com.example.moneytracker.backend.alarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.moneytracker.backend.notification.NotificationItem
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AndroidAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val userId = intent.getStringExtra("userId")
        val datasetId = intent.getStringExtra("datasetId")

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

        val dataStorage = entryPoint.dataStorage()
        val alarmManager = entryPoint.alarmManager()
        val useWorker = entryPoint.useWorker()
        val notifier = entryPoint.notifier()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Case 1: System Event (Boot or Update) or Test Action
                if (action == Intent.ACTION_BOOT_COMPLETED ||
                    action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                    action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                    action == "com.example.moneytracker.TEST_RESCHEDULE"
                ) {
                    
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val uid = currentUser.uid
                        // Fetch the user's datasets from Firestore
                        try {
                            val datasets = dataStorage.getWholeDatasets(uid, {}, {}).first()
                            datasets.forEach { dataset ->
                                if (!dataset.routine.stopRoutine) {
                                    Log.e(
                                        "AndroidAlarmReceiver",
                                        "Rescheduling for ${dataset.label}"
                                    )
                                    alarmManager.schedule(
                                        AlarmItem(
                                            dataset.id,
                                            uid,
                                            dataset.routine
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("AndroidAlarmReceiver", "Failed to reschedule", e)
                        }
                    } else {
                        Log.e("AndroidAlarmReceiver", "No current user, cannot reschedule")
                    }
                    return@launch
                }

                // Case 2: Your Custom Alarm Triggered
                if (userId.isNullOrBlank() || datasetId.isNullOrBlank()) {
                    Log.e(
                        "AndroidAlarmReceiver",
                        "Custom alarm triggered but missing extras: userId=$userId, datasetId=$datasetId"
                    )
                    return@launch
                }

                val ok = withTimeoutOrNull(15_000L) {
                    val dataset = dataStorage.getDataset(userId, datasetId)
                    if (dataset == null) {
                        alarmManager.cancel(AlarmItem(datasetId, userId, RoutineData()))
                        return@withTimeoutOrNull true
                    }

                    if (dataset.routine.stopRoutine) {
                        Log.e("AndroidAlarmReceiver", "Routine stopped for dataset: $datasetId")
                        return@withTimeoutOrNull true
                    }

                    // 2. Run the worker logic
                    useWorker.work(userId, dataset)

                    val status = dataset.status
                    val datatypeName = dataset.dataType.text
                    val label = dataset.label

                    val bigMessage = when (status) {
                        Status.SUCCESS -> "${label.title} were successfully completed"
                        Status.OVERDUE -> "${label.title} was overdue, please try to adjust your " +
                                "${datatypeName.lowercase()} for ${label.lowercase()} in time"

                        else -> throw Exception("Unknown status: $status")
                    }

                    val message = when (status) {
                        Status.SUCCESS -> "Completed: ${label.title}"
                        Status.OVERDUE -> "Overdue: ${label.title}"
                        else -> throw Exception("Unknown status: $status")
                    }



                    val notificationItem = NotificationItem(
                        title = "${datatypeName}: $label",
                        message = message,
                        bigMessage = bigMessage,
                        icon = dataset.tagIcon.icon
                    )

                    // 1. Show notification
                    notifier.showNotification(notificationItem)


                    // 3. Schedule the NEXT alarm (since AlarmManager is one-shot)
                    val alarm = AlarmItem(datasetId, userId, dataset.routine)
                    alarmManager.schedule(alarm)
                    true
                }

                if (ok == null) Log.e("AndroidAlarmReceiver", "Timeout for $datasetId")

            } catch (e: Exception) {
                Log.e("AndroidAlarmReceiver", "Error in onReceive coroutine", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
