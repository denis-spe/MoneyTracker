package com.example.moneytracker.backend.workManager

import com.example.moneytracker.backend.storage.Dataset

interface Work {
    /**
     * Schedules a work for the given [userId] and [dataset].
     */
    fun work(userId: String, dataset: Dataset)
}