package com.example.moneytracker

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MoneyTrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Check if the app is running in a debuggable environment.
        // This prevents you from accidentally using emulators in a release build.
        if (BuildConfig.DEBUG) {
            // Point to 10.0.2.2 for the Android emulator to connect to localhost.
//            Firebase.firestore.useEmulator(ip, 8080)
//            Firebase.auth.useEmulator(ip, 9099)

            val ip = "192.168.10.141" // ← your dev machine's IP
            FirebaseAuth.getInstance().useEmulator(ip, 9099)
            FirebaseFirestore.getInstance().useEmulator(ip, 8080)

            Log.d("Firebase", "Using Firebase emulators for debug build")
        } else {
            // Initialize Firebase
            Firebase.initialize(this)
            Log.d("Firebase", "Using real Firebase backend")
        }
    }
}
