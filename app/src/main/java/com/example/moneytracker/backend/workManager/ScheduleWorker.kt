// File: app/src/main/java/com/example/moneytracker/backend/workManager/ScheduleWorker.kt

package com.example.moneytracker.backend.workManager

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.moneytracker.R
import com.example.moneytracker.backend.notification.NotificationItem
import com.example.moneytracker.backend.notification.Notifier
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.getTriggerMillisFrom
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toLocalDateTime
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.helper.toMillis
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataStorage: DataStorage,
    private val notifier: Notifier,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG = "ScheduleWorker"
    }

    private val zone = ZoneId.systemDefault()

    override suspend fun doWork(): Result {
        Log.d(TAG, "ScheduleWorker started for ${params.id}")

        val datasetId = inputData.getString("datasetId") ?: return Result.failure()
        val userId = inputData.getString("userId") ?: return Result.failure()
        val isRoutine = inputData.getBoolean("isRoutine", true)

        return try {
            val dataset = dataStorage.getDataset(userId, datasetId)
                ?: return Result.failure()

            val now = java.time.LocalDateTime.now().toKotlinLocalDateTime()

            // 1. IMMEDIATELY tell the OS we are a Foreground Service
            // This prevents Huawei from killing us in the first 5 seconds
            try {
                setForeground(getForegroundInfo())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set Foreground status", e)
            }

            // ✅ Show notification immediately
            try {
                val item = showResultNotification(dataset)
                Log.d(TAG, "Showing notification: ${item.title}")
                notifier.showNotification(item)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to show notification", e)
            }

            if (isRoutine) {
                // ✅ FIX: Calculate the next trigger time based on the CURRENT deadline, not "now"
                // This prevents time drift and ensures the schedule stays consistent (e.g., always at 17:31)
                val currentDeadlineMillis = dataset.deadlineDateTime
                    .toLocalDateTimeUtc()
                    .toJavaLocalDateTime()
                    .toMillis(zone)
                val nextTrigger = dataset.routine.getTriggerMillisFrom(currentDeadlineMillis)
                val nextDeadline = nextTrigger.toLocalDateTime(zone)

                // ✅ Update Firestore asynchronously
                try {
                    dataStorage.completeRoutine(
                        userId = userId,
                        datasetId = datasetId,
                        newDateTime = now.toFirestoreTimestampUtc(),
                        nextDeadline = nextDeadline
                            .toKotlinLocalDateTime()
                            .toFirestoreTimestampUtc()
                    )
                    Log.d(TAG, "Firestore update successful for $datasetId")
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore update failed", e)
                    return Result.retry()
                }

                // ✅ CRITICAL: Schedule the NEXT work (OneTimeWorkRequest) for the next trigger
                try {
                    val delay = nextTrigger - System.currentTimeMillis()
                    if (delay > 0) {
                        Log.d(TAG, "Scheduling next work for $datasetId in ${delay}ms")
                        val data = androidx.work.Data.Builder()
                            .putString("datasetId", datasetId)
                            .putString("userId", userId)
                            .putBoolean("isRoutine", true)
                            .build()

                        val nextWorkRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(data)
                            .build()

                        WorkManager.getInstance(appContext).enqueueUniqueWork(
                            "RoutineUpdate_${datasetId}",
                            ExistingWorkPolicy.REPLACE,
                            nextWorkRequest
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to schedule next work for $datasetId", e)
                }
            }

            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error in doWork", e)
            Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = notifier.buildForegroundNotification(appContext)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // ID 42 must be unique for your app's notifications
            ForegroundInfo(
                42,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(42, notification)
        }
    }

    private fun showResultNotification(dataset: Dataset): NotificationItem {
        val status = dataset.status
        val datatypeName = dataset.dataType.text
        val label = dataset.label

        val bigMessage = when (status) {
            Status.COMPLETED -> "${label.title} were successfully completed"
            Status.OVERDUE -> "${label.title} was overdue, please to complete the goal before the deadline"
            Status.ACTIVE -> "Please adjust your ${datatypeName.lowercase()} for ${label.lowercase()}"
            else -> "Processing ${label.title}..."
        }

        val message = when (status) {
            Status.COMPLETED -> "Completed ${label.title}"
            Status.OVERDUE -> "Overdue ${label.title}"
            Status.ACTIVE -> "Adjust ${label.title}"
            else -> "Processing..."
        }

        val progressPercent = try {
            (dataset.adjustment.sumOf { it.amount } / dataset.amount * 100).toInt()
        } catch (e: Exception) {
            0
        }

        val smallIcon = R.drawable.ic_launcher_foreground
        val largeIcon = when (status) {
            Status.COMPLETED -> R.drawable.done
            Status.OVERDUE -> R.drawable.circle_error
            else -> R.drawable.pending
        }

        return NotificationItem(
            title = "${datatypeName}: $label ($progressPercent%)",
            message = message,
            bigMessage = bigMessage,
            icon = smallIcon,
            largeIcon = largeIcon
        )
    }
}
