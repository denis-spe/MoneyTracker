package com.example.moneytracker.backend.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytracker.backend.notification.AndroidNotification
import com.example.moneytracker.backend.notification.NotificationItem
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.rescheduleDeadline
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.google.firebase.Timestamp
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.toKotlinLocalDateTime
import java.util.Locale

@HiltWorker
class RoutineWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataStorage: DataStorage,
    private val workers: Workers,
    private val notifier: AndroidNotification
) : CoroutineWorker(
    appContext,
    workerParams
) {
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Routine worker started")

            val userId = inputData.getString("userId") ?: ""
            val datasetId = inputData.getString("datasetId") ?: ""

            if (userId.isEmpty() || datasetId.isEmpty()) {
                Log.e(TAG, "Invalid input data")
                return Result.failure()
            }

            Log.d(TAG, "Processing for user: $userId, dataset: $datasetId")

            // Get the dataset from the database
            val dataset = dataStorage.getDataset(userId, datasetId)

            // If the dataset is not found, return failure
            if (dataset == null) {
                Log.e(TAG, "Dataset not found")
                return Result.failure()
            } else {
                Log.d(TAG, "Dataset found: $dataset")

                val nextDeadline = dataset.routine
                    .rescheduleDeadline
                    .toKotlinLocalDateTime()
                    .toFirestoreTimestampUtc()
                val adjustSum = dataset.adjustment.sumOf { it.amount }
                val precent = ((adjustSum / dataset.amount) * 100)
                val formatPrecent = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    precent
                )


                // Complete the current routine: clear adjustment list and add status
                dataStorage.completeRoutine(
                    userId = userId,
                    datasetId = datasetId,
                    newDateTime = Timestamp.now(),
                    nextDeadline = nextDeadline
                )

                if (dataset.routine.routine != Routine.Nothing) {
                    workers.startRoutineWorker(
                        WorkersTask(
                            userId = userId,
                            datasetId = datasetId,
                            deadlineDateTime = nextDeadline,
                            routineData = dataset.routine
                        )
                    )

                    if (dataset.status != Status.ACTIVE) {
                        notifier.showNotification(
                            NotificationItem(
                                title = dataset.label,
                                message = when (dataset.status) {
                                    Status.COMPLETED -> "${dataset.label} was completed"
                                    else -> "${dataset.label} was overdue"
                                },
                                bigMessage = when (dataset.status) {
                                    Status.COMPLETED -> "Prefect, you have completed ${dataset.label} with" +
                                            " $formatPrecent%"

                                    else -> "Sorry, you failed to complete ${dataset.label} with" +
                                            " $formatPrecent%, please try to complete it in time"
                                },
                                icon = dataset.tagIcon.icon,
                                largeIcon = dataset.status.icon
                            )
                        )
                    }
                }
            }

            // Indicate that the work finished successfully
            Result.success()
        } catch (e: Exception) {
            // Handle any exceptions that occur during the work
            Log.e(TAG, "Error in RoutineWorker", e)
            Result.failure()
        }
    }


    companion object {
        const val TAG = "RoutineWorker"
    }
}