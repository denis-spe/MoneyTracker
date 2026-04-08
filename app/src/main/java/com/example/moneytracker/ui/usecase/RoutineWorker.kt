package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.workManager.UseWorker
import javax.inject.Inject

class RoutineWorker @Inject constructor(
    private val work: UseWorker,
) {
    operator fun invoke(
        userId: String,
        datasetId: String,
        triggerMillis: Long,
        isRoutine: Boolean
    ) {
        work.scheduleWork(userId, datasetId, triggerMillis, isRoutine)
    }
}