package com.example.moneytracker.backend.workManager

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
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

    override fun scheduleWork(userId: String, datasetId: String) {
        val data = Data.Builder()
            .putString("datasetId", datasetId)
            .putString("userId", userId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data)
            .build()

        // Use REPLACE policy to immediately execute new work when alarm triggers
        // This ensures immediate updates instead of queuing
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            "RoutineUpdate_${datasetId}", // Unique name per dataset
            ExistingWorkPolicy.REPLACE,  // Changed from KEEP to REPLACE for immediate execution
            workRequest
        )

        Log.d(TAG, "Work scheduled for $datasetId")
    }

    override fun rescheduleWork(userId: String) {
        val data = Data.Builder()
            .putString("userId", userId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<RescheduleWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data)
            .build()

        // Use REPLACE policy to immediately reschedule when system events occur
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            "RoutineUpdate_${userId}", // Unique name per user
            ExistingWorkPolicy.REPLACE,  // Changed from KEEP to REPLACE for immediate execution
            workRequest
        )

        Log.d(TAG, "Work rescheduled for $userId")
    }
}