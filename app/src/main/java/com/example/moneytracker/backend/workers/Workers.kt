package com.example.moneytracker.backend.workers

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

import javax.inject.Inject

class Workers @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerInf {
    override fun startRoutineWorker(workersTask: WorkersTask) {
        val data = Data.Builder()
            .putString("userId", workersTask.userId)
            .putString("datasetId", workersTask.datasetId)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<RoutineWorker>()
            .setInitialDelay(
                workersTask.calculateDelay,
                TimeUnit.MILLISECONDS
            )
            .setInputData(data)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                workersTask.datasetId,
                androidx.work.ExistingWorkPolicy.REPLACE,
                oneTimeWorkRequest
            )

    }
}