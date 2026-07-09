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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
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

@Immutable
data class CustomPalette(
    val primary: Color? = null,
    val secondarySurface: Color? = null,
    val accentContent: Color? = null,
    val onSurfaceText: Color? = null
)

@Composable
fun StewardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    lightCustomColors: CustomPalette? = null,
    darkCustomColors: CustomPalette? = null,
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

    val activeCustom = if (darkTheme) darkCustomColors else lightCustomColors

    val extendedColors = remember(
        darkTheme,
        dynamicColor,
        activeCustom
    ) {
        ExtendedColors(
            secondarySurface = (if (!dynamicColor) activeCustom?.secondarySurface else null)
                ?: themeColor(
                    darkTheme,
                    darkBackgroundColor.copy(alpha = 0.5f),
                    lightBackgroundColor.copy(alpha = 0.5f)
            ),
            accentContent = (if (!dynamicColor) activeCustom?.accentContent else null)
                ?: themeColor(
                    darkTheme,
                    Color.White.copy(alpha = 0.8f),
                    Color.Black.copy(alpha = 0.8f)
                ),
            onSurfaceText = (if (!dynamicColor) activeCustom?.onSurfaceText else null)
                ?: themeColor(
                    darkTheme,
                    Color.White,
                    Color.Black
                ),
            primary = (if (!dynamicColor) activeCustom?.primary else null)
                ?: themeColor(
                    darkTheme,
                    Color(0xFF59A5D8),
                    Color(0xFF688E26)
            )
        )
    }

    val finalColorScheme = colorScheme.copy(
        primary = extendedColors.primary,
        onPrimary = extendedColors.accentContent,
        primaryContainer = extendedColors.primary.copy(alpha = 0.12f),
        onPrimaryContainer = extendedColors.primary
    )

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = finalColorScheme,
            typography = Typography,
            content = content
        )
    }
}

object StewardTheme {
    val colors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}

private fun themeColor(isDark: Boolean, dark: Color, light: Color): Color {
    return if (isDark) dark else light
}
