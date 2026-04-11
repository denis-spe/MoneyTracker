package com.example.moneytracker.backend.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.helper.rescheduleDeadline
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class RoutineBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dataStorage: DataStorage

    @Inject
    lateinit var auth: AccountServices

    @Inject
    lateinit var workers: Workers

    override fun onReceive(context: Context, intent: Intent) {
        if (
            intent.action == "android.intent.action.BOOT_COMPLETED" ||
            intent.action == "android.intent.action.MY_PACKAGE_REPLACED" ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "android.intent.action.LOCKED_BOOT_COMPLETED"
        ) {
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
                                    deadlineDateTime = it.routine.rescheduleDeadline
                                        .toKotlinLocalDateTime()
                                        .toFirestoreTimestampUtc()
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}