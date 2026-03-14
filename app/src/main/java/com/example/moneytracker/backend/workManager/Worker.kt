package com.example.moneytracker.backend.workManager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytracker.backend.alarmManager.AlarmItem
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        val scop = CoroutineScope(Dispatchers.IO)

        val dataset = dataStorage.getDataset(userId, datasetId) ?: return Result.failure()

        val now = LocalDateTime.now()
        val alarm = AlarmItem(datasetId, userId, dataset.routine)
        val nextTrigger = alarm.triggerMillis()
        val nextDeadline =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextTrigger), ZoneId.systemDefault())


        scop.launch {
            dataStorage.addStatus(
                userId = userId,
                datasetId = datasetId,
                newDateTime = now.toFirestoreTimestampUtc(),
                newDeadlineDateTime = nextDeadline.toLocalDateTime().toKotlinLocalDateTime()
                    .toFirestoreTimestampUtc()
            )
            dataStorage.clearAdjustmentList(userId, datasetId)
        }
        return Result.success()
    }
}
