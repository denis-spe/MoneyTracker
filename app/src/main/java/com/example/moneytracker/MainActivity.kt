package com.example.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // important: call as early as possible
//        FirebaseAuth.getInstance().useEmulator("192.168.10.141", 9099)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            MoneyTrackerTheme {
                App()
//                BottomSheet(this.getString(R.string.default_web_client_id))
            }
        }
    }
}