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
import com.example.moneytracker.helper.progressPercentage
import com.example.moneytracker.helper.rescheduleDeadline
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.helper.toMidnight
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
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
                    .rescheduleDeadline()
                    .toKotlinLocalDateTime()
                    .toFirestoreTimestampUtc()

                val progress = dataset.progressPercentage
                val formatProgress = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    progress
                )

                val currentStatus = dataset.status
                Log.d(
                    TAG,
                    "Current status for ${dataset.label}: $currentStatus, progress: $formatProgress%"
                )

                // Complete the current routine: clear adjustment list and add status
                val now = Timestamp.now()
                val normalizedNow = if (dataset.routine.routine in listOf(
                        Routine.EveryDay,
                        Routine.Weekly,
                        Routine.Monthly,
                        Routine.Yearly
                    )
                ) {
                    now.toLocalDateTimeUtc().toMidnight().toFirestoreTimestampUtc()
                } else {
                    now
                }

                dataStorage.completeRoutine(
                    userId = userId,
                    datasetId = datasetId,
                    newDateTime = normalizedNow,
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

                    if (currentStatus != Status.ACTIVE && currentStatus != Status.INITIAL) {
                        val isSuccessful = currentStatus in listOf(
                            Status.COMPLETED,
                            Status.PAYBACK,
                            Status.REFUNDED,
                            Status.SUCCESS
                        )
                        notifier.showNotification(
                            NotificationItem(
                                title = dataset.label,
                                message = if (isSuccessful) "${dataset.label} was completed"
                                else "${dataset.label} was overdue",
                                bigMessage = if (isSuccessful) "Perfect, you have completed ${dataset.label} with" +
                                        " $formatProgress%"
                                else "Sorry, you failed to complete ${dataset.label} with" +
                                        " $formatProgress%, please try to complete it in time",
                                icon = dataset.tagIcon.icon,
                                largeIcon = currentStatus.icon
                            )
                        )
                    }
                }
            }

            // Indicate that the work finished successfully
            Result.success()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Firestore error in RoutineWorker", e)
            Result.retry()
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