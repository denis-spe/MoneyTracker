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

        setContent {
            val themeConfig by settingsViewModel.themeConfig.collectAsState()
            val dynamicColor by settingsViewModel.dynamicColor.collectAsState()

            // Light palette
            val lSecondarySurface by settingsViewModel.lightSecondarySurface.collectAsState()
            val lAccentContent by settingsViewModel.lightAccentContent.collectAsState()
            val lOnSurfaceText by settingsViewModel.lightOnSurfaceText.collectAsState()
            val lPrimaryAccent by settingsViewModel.lightPrimaryAccent.collectAsState()

            // Dark palette
            val dSecondarySurface by settingsViewModel.darkSecondarySurface.collectAsState()
            val dAccentContent by settingsViewModel.darkAccentContent.collectAsState()
            val dOnSurfaceText by settingsViewModel.darkOnSurfaceText.collectAsState()
            val dPrimaryAccent by settingsViewModel.darkPrimaryAccent.collectAsState()

            val darkTheme = when (themeConfig) {
                ThemeConfig.SYSTEM -> isSystemInDarkTheme()
                ThemeConfig.LIGHT -> false
                ThemeConfig.DARK -> true
            }

            MoneyTrackerTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor,
                lightCustomColors = CustomPalette(
                    primaryAccent = lPrimaryAccent?.let { Color(it.toULong()) },
                    secondarySurface = lSecondarySurface?.let { Color(it.toULong()) },
                    accentContent = lAccentContent?.let { Color(it.toULong()) },
                    onSurfaceText = lOnSurfaceText?.let { Color(it.toULong()) }
                ),
                darkCustomColors = CustomPalette(
                    primaryAccent = dPrimaryAccent?.let { Color(it.toULong()) },
                    secondarySurface = dSecondarySurface?.let { Color(it.toULong()) },
                    accentContent = dAccentContent?.let { Color(it.toULong()) },
                    onSurfaceText = dOnSurfaceText?.let { Color(it.toULong()) }
                )
            ) {
                App()
            }
        }
    }
}
