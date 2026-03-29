// Bless be the name of the LORD of hosts
package com.example.moneytracker.backend.alarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.moneytracker.R
import com.example.moneytracker.backend.notification.NotificationItem
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class AndroidAlarmReceiver : BroadcastReceiver() {

    private fun showResultNotification(dataset: Dataset): NotificationItem {
        val status = dataset.status
        val datatypeName = dataset.dataType.text
        val label = dataset.label

        val bigMessage = when (status) {
            Status.COMPLETED -> "${label.title} were successfully completed"
            Status.OVERDUE -> "${label.title} was overdue, please try to adjust your " +
                    "${datatypeName.lowercase()} for ${label.lowercase()} in time"

            Status.PENDING -> "Please adjust your ${datatypeName.lowercase()} for ${label.lowercase()}"
            else -> "Processing ${label.title}..."
        }

        val message = when (status) {
            Status.COMPLETED -> "Completed ${label.title}"
            Status.OVERDUE -> "Overdue ${label.title}"
            Status.PENDING -> "Adjust ${label.title}"
            else -> "Processing ${label.title}..."
        }

        val progressPercent =
            (dataset.adjustment.sumOf { it.amount } / dataset.amount * 100).toInt()

        val iconRes = when (status) {
            Status.COMPLETED -> R.drawable.done
            Status.OVERDUE -> R.drawable.circle_error
            else -> R.drawable.pending
        }

        val goalIcon = dataset.tagIcon.icon

        val item = NotificationItem(
            title = "${datatypeName}: $label ($progressPercent%)",
            message = message,
            bigMessage = bigMessage,
            icon = goalIcon,
            largeIcon = iconRes
        )
        return item
    }


    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val userId = intent.getStringExtra("userId")
        val datasetId = intent.getStringExtra("datasetId")

        // Use Log.e because Huawei devices often suppress Log.d by default
        val pendingResult = goAsync()

        val entryPoint = try {
            val entry = EntryPointAccessors.fromApplication(
                context.applicationContext,
                AlarmReceiverEntryPoint::class.java
            )

            val dataStorage = entry.dataStorage()
            val notifier = entry.notifier()
            val scope = CoroutineScope(Dispatchers.IO)
            if (userId == null || datasetId == null) return

            scope.launch {
                val now = java.time.LocalDateTime.now().toKotlinLocalDateTime()
                val dataset = dataStorage.getDataset(userId, datasetId) ?: return@launch

                val alarm = AlarmItem(datasetId, userId, dataset.routine)
                val nextTrigger = alarm.triggerMillis()
                val nextDeadline = ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(nextTrigger),
                    ZoneId.systemDefault()
                )

                dataStorage.completeRoutine(
                    userId = userId,
                    datasetId = datasetId,
                    newDateTime = now.toFirestoreTimestampUtc(),
                    nextDeadline = nextDeadline.toLocalDateTime()
                        .toKotlinLocalDateTime()
                        .toFirestoreTimestampUtc()
                )

            }

            scope.launch {
                val dataset = dataStorage.getDataset(userId, datasetId) ?: return@launch

                // ✅ Show notification (THIS is what you actually want)
                val item = showResultNotification(dataset)
                notifier.showNotification(item)
            }

            Log.d("AndroidAlarmReceiver", "Hilt EntryPoint success")

            entry
        } catch (e: Exception) {
            Log.e("AndroidAlarmReceiver", "Hilt EntryPoint failed", e)
            pendingResult.finish()
            return
        }

        val dataStorage = entryPoint.dataStorage()
        val alarmManager = entryPoint.alarmManager()
        val useWorker = entryPoint.useWorker()

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

                    // 2. Run the worker logic (Worker now handles the notification in getForegroundInfo)
                    useWorker.work(userId, dataset)

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
