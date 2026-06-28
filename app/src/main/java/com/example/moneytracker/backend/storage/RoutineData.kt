/**
 * Holy, holy, holy is the LORD of hosts the whole earth
 * is full of his glory
 */
package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp

data class RoutineData(
    var routine: Routine = Routine.Nothing,
    val routineCount: Int = 0,
    val stopRoutine: Boolean = true,
    val startDateTime: Timestamp = Timestamp.now(),
    val deadlineDateTime: Timestamp = Timestamp.now(),
    val triggerMillis: Long = 0,
    val specificDays: List<Int> = emptyList()
)
