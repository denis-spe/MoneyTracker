package com.example.moneytracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        val isDebug = 0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
//
//        if (isDebug) {
//            val ip = "192.168.10.141" // ← your dev machine's IP
//            FirebaseAuth.getInstance().useEmulator(ip, 9099)
//            FirebaseFirestore.getInstance().useEmulator(ip, 8080)
//
//            // Optional:
//            // FirebaseStorage.getInstance().useEmulator(ip, 9199)
//            Log.d("Firebase", "Using Firebase emulators for debug build")
//        } else {
//            // Initialize Firebase
//            FirebaseApp.initializeApp(this)
//            Log.d("Firebase", "Using real Firebase backend")
//        }


        setContent {
            MoneyTrackerTheme {
                App()
            }
        }
    }
}