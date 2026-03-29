package com.example.moneytracker.backend.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytracker.R
import com.example.moneytracker.backend.notification.NotificationItem
import com.example.moneytracker.backend.notification.Notifier
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

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
    private val item: NotificationItem? = null

    override suspend fun doWork(): Result {
        Log.e(TAG, "Worker started for ${params.id}")

        val datasetId = inputData.getString("datasetId") ?: return Result.failure()
        val userId = inputData.getString("userId") ?: return Result.failure()

        return try {
            val dataset = dataStorage.getDataset(userId, datasetId)
                ?: return Result.failure()

            Log.e(TAG, "Routine update successful for $datasetId")

            // ✅ Show notification (THIS is what you actually want)
            val item = showResultNotification(dataset)
            notifier.showNotification(item)

            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error in doWork for $datasetId", e)
            Result.retry()
        }
    }

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
}
