package com.example.moneytracker.backend.alarmManager

import android.util.Log
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.helper.getTriggerMillisFrom
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRescheduler @Inject constructor(
    private val dataStorage: DataStorage,
    private val alarmManager: AndroidAlarmManager,
    private val auth: FirebaseAuth
) {
    suspend fun reschedule() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            Log.e("AlarmRescheduler", "Starting reschedule for user: $uid")
            try {
                val datasets = dataStorage.getWholeDatasets(uid, {}, {}).first()
                datasets.forEach { dataset ->
                    if (!dataset.routine.stopRoutine) {
                        Log.e(
                            "AlarmRescheduler",
                            "Rescheduling alarm for dataset: ${dataset.label}"
                        )
                        alarmManager.schedule(
                            AlarmItem(
                                dataset.id,
                                uid,
                                dataset.routine.getTriggerMillisFrom(dataset.deadlineDateTime.toDate().time)
                            )
                        )
                    }
                }
                Log.e(
                    "AlarmRescheduler",
                    "Successfully rescheduled ${datasets.size} potential alarms"
                )
            } catch (e: Exception) {
                Log.e("AlarmRescheduler", "Failed to reschedule alarms", e)
            }
        } else {
            Log.e("AlarmRescheduler", "No current user, skipping reschedule")
        }
    }
}
