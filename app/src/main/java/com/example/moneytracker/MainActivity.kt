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
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.ui.settings.SettingsViewModel
import com.example.moneytracker.ui.settings.ThemeConfig
import com.example.moneytracker.ui.theme.CustomPalette
import com.example.moneytracker.ui.theme.StewardTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) Log.d("MainActivity", "Notification permission granted")
        else Log.w("MainActivity", "Notification permission denied")
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {  // no @RequiresApi here
        super.onCreate(savedInstanceState)
        StartupTimer.mark("Activity.onCreate")

        enableEdgeToEdge()
        checkNotificationPermission()

        setContent {
            StartupTimer.mark("setContent composed")
            val themeState by settingsViewModel.themeState.collectAsState()

            val darkTheme = when (themeState.themeConfig) {
                ThemeConfig.SYSTEM -> isSystemInDarkTheme()
                ThemeConfig.LIGHT -> false
                ThemeConfig.DARK -> true
            }

            StewardTheme(
                darkTheme = darkTheme,
                dynamicColor = themeState.dynamicColor,
                lightCustomColors = CustomPalette(
                    primary = themeState.lightPrimaryAccent?.let { Color(it.toULong()) },
                    secondarySurface = themeState.lightSecondarySurface?.let { Color(it.toULong()) },
                    accentContent = themeState.lightAccentContent?.let { Color(it.toULong()) },
                    onSurfaceText = themeState.lightOnSurfaceText?.let { Color(it.toULong()) }
                ),
                darkCustomColors = CustomPalette(
                    primary = themeState.darkPrimaryAccent?.let { Color(it.toULong()) },
                    secondarySurface = themeState.darkSecondarySurface?.let { Color(it.toULong()) },
                    accentContent = themeState.darkAccentContent?.let { Color(it.toULong()) },
                    onSurfaceText = themeState.darkOnSurfaceText?.let { Color(it.toULong()) }
                )
            ) {
                // reportFullyDrawn is passed as a callback — HomeScreen calls it
                // when isDataLoaded turns true using the ViewModel it already has
                App(
                    onFullyDrawn = { reportFullyDrawn() }
                )
            }
        }
    }
}
