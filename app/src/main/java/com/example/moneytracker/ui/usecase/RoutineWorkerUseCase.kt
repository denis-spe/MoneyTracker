// Holy, holy, holy is the LORD of hosts the whole earth is full of his glory
package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.workers.Workers
import com.example.moneytracker.backend.workers.WorkersTask
import com.google.firebase.Timestamp
import javax.inject.Inject

class RoutineWorkerUseCase @Inject constructor(
    private val workers: Workers,
) {
    operator fun invoke(
        userId: String,
        datasetId: String,
        triggerMillis: Long,
        isRoutine: Boolean
    ) {
        workers.startRoutineWorker(
            WorkersTask(
                userId = userId,
                datasetId = datasetId,
                deadlineDateTime = Timestamp(
                    triggerMillis / 1000,
                    ((triggerMillis % 1000) * 1000000).toInt()
                ),
                routineData = if (isRoutine) RoutineData() else RoutineData() // RoutineData will be handled by WorkersTask logic
            )
        )
    }
}