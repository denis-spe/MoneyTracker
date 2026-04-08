package com.example.moneytracker.backend.workManager

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseWorker @Inject constructor(
    @ApplicationContext private val appContext: Context
) : Work {
    companion object {
        const val TAG = "UseWorker"
    }

    override fun scheduleWork(
        userId: String,
        datasetId: String,
        triggerMillis: Long,
        isRoutine: Boolean
    ) {
        val data = Data.Builder()
            .putString("datasetId", datasetId)
            .putString("userId", userId)
            .putBoolean("isRoutine", isRoutine)
            .build()

        val delay = triggerMillis - System.currentTimeMillis()

        val workRequestBuilder = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInputData(data)

        if (delay > 0) {
            workRequestBuilder.setInitialDelay(delay, TimeUnit.MILLISECONDS)
        } else if (isRoutine) {
            // If the time has already passed for a routine, run it as expedited to catch up
            workRequestBuilder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        }

        val workRequest = workRequestBuilder.build()

        // Use REPLACE policy to immediately execute/update the work
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            if (isRoutine) "RoutineUpdate_${datasetId}" else "GoalUpdate_${datasetId}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Log.d(
            TAG,
            "Work scheduled for $datasetId at $triggerMillis (delay: $delay ms, isRoutine: $isRoutine)"
        )
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