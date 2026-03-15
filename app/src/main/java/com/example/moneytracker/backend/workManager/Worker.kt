package com.example.moneytracker.backend.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytracker.backend.alarmManager.AlarmItem
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@HiltWorker
class Worker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataStorage: DataStorage // ✅ Injected by Hilt
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val datasetId = params.inputData.getString("datasetId")
            ?: return Result.failure()
        val userId = params.inputData.getString("userId")
            ?: return Result.failure()


        return try {
            val dataset = dataStorage.getDataset(userId, datasetId) ?: return Result.failure()

            val now = LocalDateTime.now()
            val alarm = AlarmItem(datasetId, userId, dataset.routine)
            val nextTrigger = alarm.triggerMillis()
            val nextDeadline =
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextTrigger), ZoneId.systemDefault())

            dataStorage.completeRoutine(
                userId = userId,
                datasetId = datasetId,
                newDateTime = now.toFirestoreTimestampUtc(),
                nextDeadline = nextDeadline.toLocalDateTime().toKotlinLocalDateTime()
                    .toFirestoreTimestampUtc()
            )

            Result.success()
        } catch (e: Exception) {
            Log.e("Worker", "Error in doWork for dataset $datasetId", e)
            Result.retry()
        }
    }
}
