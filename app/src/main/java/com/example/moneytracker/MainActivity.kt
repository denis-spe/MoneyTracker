package com.example.moneytracker

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isDebug = 0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)



        if (isDebug) {
            val ip = "192.168.10.141" // ← your dev machine's IP
            FirebaseAuth.getInstance().useEmulator(ip, 9099)
            FirebaseFirestore.getInstance().useEmulator(ip, 8080)

            // Optional:
            // FirebaseStorage.getInstance().useEmulator(ip, 9199)
            Log.d("Firebase", "Using Firebase emulators for debug build")
        } else {
            Log.d("Firebase", "Using real Firebase backend")
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            MoneyTrackerTheme {
                App()
            }
        }
    }
}