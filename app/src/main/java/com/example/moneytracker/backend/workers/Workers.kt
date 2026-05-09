package com.example.moneytracker.backend.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Workers @Inject constructor(
    @param:ApplicationContext private val context: Context
) : WorkerInf {

    override fun startRoutineWorker(workersTask: WorkersTask) {
        try {
            val calculatedDelay = workersTask.calculateDelay

            Log.d(
                TAG,
                "Scheduling worker for dataset=${workersTask.datasetId}, " +
                        "delay=${calculatedDelay}ms (${calculatedDelay / 1000 / 60} minutes)"
            )

            if (calculatedDelay < 0) {
                Log.w(TAG, "Calculated delay is negative, using 0ms")
            }

            // Ensure delay is non-negative
            val finalDelay = maxOf(0L, calculatedDelay)

            val data = Data.Builder()
                .putString("userId", workersTask.userId)
                .putString("datasetId", workersTask.datasetId)
                .putString("financeType", workersTask.financeType)
                .build()

            val oneTimeWorkRequest = OneTimeWorkRequestBuilder<RoutineWorker>()
                .setInitialDelay(finalDelay, TimeUnit.MILLISECONDS)
                .setInputData(data)
//                .setBackoffCriteria(
//                    BackoffPolicy.EXPONENTIAL,
//                    MIN_BACKOFF_MILLIS,
//                    TimeUnit.MILLISECONDS
//                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    workersTask.datasetId,
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    oneTimeWorkRequest
                )

            Log.d(TAG, "Worker enqueued successfully for ${workersTask.datasetId}")

        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling routine worker", e)
        }
    }

    override fun cancelAllWorkers() {
        try {
            WorkManager.getInstance(context).cancelAllWork()
            Log.d(TAG, "All routine workers canceled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error canceling routine workers", e)
        }
    }

    companion object {
        private const val TAG = "Workers"
        private const val MIN_BACKOFF_MILLIS = 15 * 60 * 1000L // 15 minutes
    }
}