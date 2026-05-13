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
    val primaryAccent: Color? = null,
    val secondarySurface: Color? = null,
    val accentContent: Color? = null,
    val onSurfaceText: Color? = null
)

@Composable
fun MoneyTrackerTheme(
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
            primaryAccent = (if (!dynamicColor) activeCustom?.primaryAccent else null)
                ?: themeColor(
                    darkTheme,
                    Color(0xFF59A5D8),
                    Color(0xFF688E26)
            )
        ).also {
            MoneyTrackerTheme.setAppColors(it)
        }
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
        get() = appColors ?: LocalExtendedColors.current

    private var appColors: ExtendedColors? = null

    internal fun setAppColors(colors: ExtendedColors) {
        appColors = colors
    }

    /**
     * Returns [light] if the system is in light theme, and [dark] otherwise.
     */
    @Composable
    fun <T> composableColor(light: T? = null, dark: T? = null): T? {
        val result = if (isSystemInDarkTheme()) dark else light
        if (result is ExtendedColors) {
            val current = appColors ?: LocalExtendedColors.current
            appColors = current.copy(
                secondarySurface = if (result.secondarySurface != Color.Unspecified) result.secondarySurface else current.secondarySurface,
                accentContent = if (result.accentContent != Color.Unspecified) result.accentContent else current.accentContent,
                onSurfaceText = if (result.onSurfaceText != Color.Unspecified) result.onSurfaceText else current.onSurfaceText,
                primaryAccent = if (result.primaryAccent != Color.Unspecified) result.primaryAccent else current.primaryAccent
            )
        }
        return result
    }
}

private fun themeColor(isDark: Boolean, dark: Color, light: Color): Color {
    return if (isDark) dark else light
}
