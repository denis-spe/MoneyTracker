package com.example.moneytracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.example.moneytracker.ui.settings.SettingsViewModel
import com.example.moneytracker.ui.settings.ThemeConfig
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.w("MainActivity", "Notification permission denied")
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()


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
            val themeConfig by settingsViewModel.themeConfig.collectAsState()
            val dynamicColor by settingsViewModel.dynamicColor.collectAsState()

            val darkTheme = when (themeConfig) {
                ThemeConfig.SYSTEM -> isSystemInDarkTheme()
                ThemeConfig.LIGHT -> false
                ThemeConfig.DARK -> true
            }

            MoneyTrackerTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            ) {
                App()
            }
        }
    }
}
