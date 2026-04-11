package com.example.moneytracker.backend.workers

interface WorkerInf {
    fun startRoutineWorker(workersTask: WorkersTask)
}