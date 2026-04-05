/**
 * Holy, holy, holy is the LORD of hosts the whole earth
 * is full of his glory
 */
package com.example.moneytracker.backend.storage

data class RoutineData(
    var routine: Routine = Routine.Nothing,
    val routineCount: Int = 0,
    val stopRoutine: Boolean = true
)
