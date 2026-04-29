package com.example.moneytracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.workers.Workers
import com.example.moneytracker.backend.workers.WorkersTask
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MoneyTrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workers: Workers

    @Inject
    lateinit var auth: AccountServices

    @Inject
    lateinit var dataStorage: DataStorage


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (!auth.hasUser) return
        val scope = CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        scope.launch {
            val userId = auth.currentUserId
            val datasets = dataStorage.getWholeDatasets(userId, {}, {})
            datasets.collect { datasetList ->
                datasetList.forEach {
                    if (it.routine.routine != Routine.Nothing) {
                        workers.startRoutineWorker(
                            WorkersTask(
                                userId = userId,
                                datasetId = it.id,
                                routineData = it.routine,
                                deadlineDateTime = it.routine.deadlineDateTime
                            )
                        )
                    }
                }
            }
        }
    }
}
