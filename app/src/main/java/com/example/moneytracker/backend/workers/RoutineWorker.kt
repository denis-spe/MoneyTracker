package com.example.moneytracker.backend.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytracker.backend.notification.AndroidNotification
import com.example.moneytracker.backend.notification.NotificationItem
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.formatedDateTime
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
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

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
        Log.d(TAG, "================ WORKER START ================")

        return try {
            val userId = inputData.getString("userId") ?: ""
            val datasetId = inputData.getString("datasetId") ?: ""
            val financeType = inputData.getString("financeType") ?: ""

            Log.d(TAG, "Input Data: userId=$userId datasetId=$datasetId financeType=$financeType")

            if (userId.isEmpty() || datasetId.isEmpty() || financeType.isEmpty()) {
                Log.e(TAG, "Invalid input data")
                return Result.failure()
            }

            Log.d(TAG, "Fetching dataset...")
            val dataset = dataStorage.getDataset(userId, datasetId, financeType)

            Log.d(TAG, "Dataset fetched: $dataset")

            if (dataset !is FinanceEntity.Goal) {
                Log.e(TAG, "Dataset is not a Goal")
                return Result.failure()
            }

            val nextDeadline = dataset.routine
                .rescheduleDeadline(
                    baseTime = dataset.routine.deadlineDateTime
                        .toLocalDateTimeUtc()
                        .toJavaLocalDateTime()
                )
                .toKotlinLocalDateTime()
                .toFirestoreTimestampUtc()

            Log.d(TAG, "Next deadline: $nextDeadline")

            val progress = dataset.progressPercentage
            val formatProgress = String.format(Locale.getDefault(), "%.1f", progress)
            val currentStatus = dataset.status

            Log.d(TAG, "Current status: $currentStatus")
            Log.d(TAG, "Progress: $formatProgress%")

            val now = Timestamp.now()
            val normalizedNow = if (dataset.routine.routine in listOf(
                    Routine.EveryDay,
                    Routine.Weekly,
                    Routine.Monthly,
                    Routine.Yearly,
                    Routine.SpecifyDayOfTheWeek
                )
            ) {
                now.toLocalDateTimeUtc().toMidnight().toFirestoreTimestampUtc()
            } else {
                now
            }

            Log.d(TAG, "Normalized now: $normalizedNow")
            Log.d(TAG, "Calling completeRoutine()...")

            dataStorage.completeRoutine(
                userId = userId,
                datasetId = datasetId,
                financeType = financeType,
                newDateTime = normalizedNow,
                nextDeadline = nextDeadline,
            )

            Log.d(TAG, "Returned from completeRoutine()")

            if (dataset.routine.routine != Routine.Nothing) {
                Log.d(TAG, "Scheduling next worker...")
                workers.startRoutineWorker(
                    WorkersTask(
                        userId = userId,
                        datasetId = datasetId,
                        financeType = financeType,
                        deadlineDateTime = nextDeadline,
                        routineData = dataset.routine
                    )
                )
                Log.d(TAG, "Worker rescheduled")

                if (currentStatus != Status.ACTIVE && currentStatus != Status.INITIAL) {
                    val isSuccessful = currentStatus in listOf(
                        Status.COMPLETED,
                        Status.PAYBACK,
                        Status.REFUNDED,
                        Status.SUCCESS
                    )

                    Log.d(TAG, "Triggering notification... success=$isSuccessful")

                    notifier.showNotification(
                        NotificationItem(
                            title = dataset.label,
                            message = if (isSuccessful) "Goal completed!" else "Deadline missed",
                            bigMessage = if (isSuccessful) {
                                "Perfect! You've completed ${dataset.label} with ${formatProgress}%."
                            } else {
                                "You didn't finish ${dataset.label} (${formatProgress}%). " +
                                        "Please try to complete it before the next deadline on " +
                                        "${nextDeadline.toLocalDateTimeUtc().formatedDateTime}."
                            },
                            icon = dataset.tagIcon.icon,
                            largeIcon = currentStatus.icon
                        )
                    )

                    Log.d(TAG, "Notification sent")
                } else {
                    Log.d(TAG, "Notification skipped due to status: $currentStatus")
                }
            } else {
                Log.d(TAG, "Routine is NOTHING -> skipping reschedule and notification")
            }

            Log.d(TAG, "================ WORKER SUCCESS ================")
            Result.success()
        } catch (e: CancellationException) {
            Log.w(TAG, "Worker CANCELLED", e)
            throw e
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Firestore error in RoutineWorker", e)
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Error in RoutineWorker", e)
            Result.failure()
        }
    }


    companion object {
        const val TAG = "RoutineWorker"
    }
}