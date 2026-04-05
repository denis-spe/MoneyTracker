package com.example.moneytracker

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.moneytracker.backend.alarmManager.AlarmRescheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MoneyTrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var alarmRescheduler: AlarmRescheduler

    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Reschedule alarms on every app start (handles Android Studio "Run")
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("MoneyTrackerApp", "App started, triggering reschedule...")
            alarmRescheduler.reschedule()
        }
    }
}
