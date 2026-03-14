// Bless be the name of the LORD of hosts
package com.example.moneytracker.backend.alarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.moneytracker.backend.storage.RoutineData
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AndroidAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val action = intent.action
        val userId = intent.getStringExtra("userId")
        val datasetId = intent.getStringExtra("datasetId")

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmReceiverEntryPoint::class.java
        )
        val dataStorage = entryPoint.dataStorage()
        val alarmManager = entryPoint.alarmManager()
        val useWorker = entryPoint.useWorker()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Case 1: System Event (Boot or Update)
                if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
                    Log.d("AndroidAlarmReceiver", "Rescheduling alarms due to: $action")
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val uid = currentUser.uid
                        // Fetch the user's datasets from Firestore
                        try {
                            val datasets = dataStorage.getWholeDatasets(uid, {}, {}).first()
                            datasets.forEach { dataset ->
                                if (!dataset.routine.stopRoutine) {
                                    Log.d(
                                        "AndroidAlarmReceiver",
                                        "Rescheduling for ${dataset.label}"
                                    )
                                    alarmManager.schedule(
                                        AlarmItem(
                                            dataset.id,
                                            uid,
                                            dataset.routine
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("AndroidAlarmReceiver", "Failed to reschedule", e)
                        }
                    }
                    return@launch
                }

                // Case 2: Your Custom Alarm Triggered
                if (userId.isNullOrBlank() || datasetId.isNullOrBlank()) return@launch

                val ok = withTimeoutOrNull(15_000L) {
                    val dataset = dataStorage.getDataset(userId, datasetId)
                    if (dataset == null) {
                        alarmManager.cancel(AlarmItem(datasetId, userId, RoutineData()))
                        return@withTimeoutOrNull true
                    }

                    if (dataset.routine.stopRoutine) return@withTimeoutOrNull true

                    // 1. Run the worker logic
                    useWorker.work(userId, dataset)

                    // 2. Schedule the NEXT alarm (since AlarmManager is one-shot)
                    val alarm = AlarmItem(datasetId, userId, dataset.routine)
                    alarmManager.schedule(alarm)
                    true
                }

                if (ok == null) Log.w("AndroidAlarmReceiver", "Timeout for $datasetId")

            } catch (e: Exception) {
                Log.e("AndroidAlarmReceiver", "Error", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
