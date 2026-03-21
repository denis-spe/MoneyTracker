package com.example.moneytracker.backend.workManager

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.moneytracker.R
import com.example.moneytracker.backend.alarmManager.AlarmItem
import com.example.moneytracker.backend.notification.NotificationItem
import com.example.moneytracker.backend.notification.Notifier
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@HiltWorker
class Worker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataStorage: DataStorage,
    private val notifier: Notifier,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG = "RoutineWorker"
        const val NOTIFICATION_ID = 42
    }

    private var cachedDataset: Dataset? = null

    override suspend fun doWork(): Result {
        Log.e(TAG, "Worker started for ${params.id}")

        val datasetId = inputData.getString("datasetId")
        val userId = inputData.getString("userId")

        if (datasetId == null || userId == null) {
            Log.e(TAG, "Missing input data: datasetId=$datasetId, userId=$userId")
            return Result.failure()
        }

        try {
            setForeground(getForegroundInfo())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "Failed to set foreground", e)
        }

        return try {
            val dataset = cachedDataset ?: dataStorage.getDataset(userId, datasetId)

            if (dataset == null) {
                Log.e(TAG, "Dataset not found: $datasetId")
                return Result.failure()
            }

            val now = java.time.LocalDateTime.now().toKotlinLocalDateTime()
            
            val alarm = AlarmItem(datasetId, userId, dataset.routine)
            val nextTrigger = alarm.triggerMillis()
            val nextDeadline =
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextTrigger), ZoneId.systemDefault())

            Log.e(TAG, "Completing routine for $datasetId. Next deadline: $nextDeadline")

            dataStorage.completeRoutine(
                userId = userId,
                datasetId = datasetId,
                newDateTime = now.toFirestoreTimestampUtc(),
                nextDeadline = nextDeadline.toLocalDateTime().toKotlinLocalDateTime()
                    .toFirestoreTimestampUtc()
            )

            Log.e(TAG, "Routine update successful for $datasetId")

            // Post a persistent notification so it doesn't disappear when the worker finishes
            showResultNotification(dataset)
            
            Result.success()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "Error in doWork for $datasetId", e)
            Result.retry()
        }
    }

    private fun showResultNotification(dataset: Dataset) {
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

        val progressPercent = dataset.let {
            it.adjustment.sumOf { adjust -> adjust.amount } / it.amount
        }

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
        notifier.showNotification(item)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {

        val notification = NotificationCompat.Builder(appContext, notifier.channelId)
            .setSmallIcon(R.drawable.initial)
            .setContentTitle("") // Keep it empty as per your requirement
            .setContentText("")
            .setPriority(NotificationCompat.PRIORITY_MIN) // MIN priority hides the icon from status bar on many devices
            .setSilent(true) // No sound or vibration
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }
}
