package com.example.moneytracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MoneyTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val darkBackgroundColor = Color(0xFF282626)
    val lightBackgroundColor = Color(0xFFE0DDDD)

    val extendedColors = if (darkTheme) {
        ExtendedColors(
            customBackground = darkBackgroundColor.copy(alpha = 0.5f),
            contentColor = Color.White.copy(alpha = 0.8f),
            currentPage = Color(0xFF8F8686).copy(alpha = 0.2f),
            autoBackground = darkBackgroundColor,
            autoText = Color.White,
            themeColor = Color(0xFF11575E)
        )
    } else {
        ExtendedColors(
            customBackground = lightBackgroundColor.copy(alpha = 0.5f),
            contentColor = Color.Black.copy(alpha = 0.8f),
            currentPage = Color(0xFF8C8B8B).copy(alpha = 0.2f),
            autoBackground = lightBackgroundColor,
            autoText = Color.Black,
            themeColor = Color(0xFF688E26)
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object MoneyTrackerTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}
