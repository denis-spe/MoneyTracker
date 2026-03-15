package com.example.moneytracker

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.moneytracker.backend.alarmManager.AlarmRescheduler
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize
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
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Check if the app is running in a debuggable environment.
        if (BuildConfig.DEBUG) {
            val ip = "192.168.10.141" // ← your dev machine's IP
            FirebaseAuth.getInstance().useEmulator(ip, 9099)
            FirebaseFirestore.getInstance().useEmulator(ip, 8080)
            Log.d("Firebase", "Using Firebase emulators for debug build")
        } else {
            Firebase.initialize(this)
            Log.d("Firebase", "Using real Firebase backend")
        }

        // Reschedule alarms on every app start (handles Android Studio "Run")
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("MoneyTrackerApp", "App started, triggering reschedule...")
            alarmRescheduler.reschedule()
        }
    }
}
