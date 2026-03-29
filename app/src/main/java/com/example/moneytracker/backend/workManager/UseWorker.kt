package com.example.moneytracker.backend.workManager

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.moneytracker.backend.storage.Dataset
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseWorker @Inject constructor(
    @ApplicationContext private val appContext: Context
) : Work {
    companion object {
        const val TAG = "UseWorker"
    }

    override fun work(userId: String, dataset: Dataset) {
        val data = Data.Builder()
            .putString("datasetId", dataset.id)
            .putString("userId", userId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<Worker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data)
            .build()

        // Use Unique Work to avoid queuing delays
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            "RoutineUpdate_${dataset.id}", // Unique name per dataset
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}