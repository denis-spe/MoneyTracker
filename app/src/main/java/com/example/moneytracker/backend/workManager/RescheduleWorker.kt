package com.example.moneytracker.backend.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytracker.backend.alarmManager.AlarmItem
import com.example.moneytracker.backend.alarmManager.AndroidAlarmManager
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.helper.triggerMillis
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first


@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataStorage: DataStorage,
    private val alarmManager: AndroidAlarmManager
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
                    Log.e(
                        "AndroidAlarmReceiver",
                        "Rescheduling for ${dataset.label}"
                    )
                    alarmManager.schedule(
                        AlarmItem(
                            dataset.id,
                            userId,
                            dataset.routine.triggerMillis
                        )
                    )
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