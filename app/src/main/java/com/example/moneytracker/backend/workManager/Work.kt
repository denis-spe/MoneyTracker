package com.example.moneytracker.backend.workManager

interface Work {
    /**
     * Schedules a work for the given [userId] and [datasetId].
     */
    fun scheduleWork(userId: String, datasetId: String, triggerMillis: Long, isRoutine: Boolean)

    fun rescheduleWork(userId: String)
}