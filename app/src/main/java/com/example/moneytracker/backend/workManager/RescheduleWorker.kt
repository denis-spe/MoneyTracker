package com.example.moneytracker.backend.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.helper.triggerMillis
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit


@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataStorage: DataStorage,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG = "RescheduleWorker"
    }

    override suspend fun doWork(): Result {
        Log.e(TAG, "RescheduleWorker started for ${params.id}")

        val userId = inputData.getString("userId") ?: return Result.failure()

        return try {
            val datasets = dataStorage.getWholeDatasets(userId, {}, {}).first()
            datasets.forEach { dataset ->
                if (!dataset.routine.stopRoutine) {
                    val triggerMillis = dataset.routine.triggerMillis
                    val delay = triggerMillis - System.currentTimeMillis()

                    val data = Data.Builder()
                        .putString("datasetId", dataset.id)
                        .putString("userId", userId)
                        .build()

                    val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
                        .setInitialDelay(delay.coerceAtLeast(0), TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .build()

                    WorkManager.getInstance(appContext).enqueueUniqueWork(
                        "RoutineUpdate_${dataset.id}",
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )
                    Log.d(TAG, "Rescheduled work for ${dataset.label} at $triggerMillis")
                }
            }


            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error in doWork for $userId", e)
            Result.retry()
        }
    }
}