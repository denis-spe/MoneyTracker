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
    customThemeColor: Color? = null,
    customBackgroundColor: Color? = null,
    customContentColor: Color? = null,
    customAutoBackgroundColor: Color? = null,
    customAutoTextColor: Color? = null,
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

    val extendedColors = ExtendedColors(
        customBackground = themeColor(
            darkTheme,
            darkBackgroundColor.copy(alpha = 0.5f),
            lightBackgroundColor.copy(alpha = 0.5f)
        ),
        contentColor = themeColor(
            darkTheme,
            Color.White.copy(alpha = 0.8f),
            Color.Black.copy(alpha = 0.8f)
        ),
        autoBackground = themeColor(darkTheme, darkBackgroundColor, lightBackgroundColor),
        autoText = themeColor(darkTheme, Color.White, Color.Black),
        themeColor = themeColor(darkTheme, Color(0xFF59A5D8), Color(0xFF688E26))
    )

    if (!dynamicColor) {
        MoneyTrackerTheme.composableColor(
            light = ExtendedColors(
                customBackground = customBackgroundColor ?: Color.Unspecified,
                contentColor = customContentColor ?: Color.Unspecified,
                autoBackground = customAutoBackgroundColor ?: Color.Unspecified,
                autoText = customAutoTextColor ?: Color.Unspecified,
                themeColor = customThemeColor ?: Color.Unspecified
            ),
            dark = ExtendedColors(
                customBackground = customBackgroundColor ?: Color.Unspecified,
                contentColor = customContentColor ?: Color.Unspecified,
                autoBackground = customAutoBackgroundColor ?: Color.Unspecified,
                autoText = customAutoTextColor ?: Color.Unspecified,
                themeColor = customThemeColor ?: Color.Unspecified
            )
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
        get() = appColors ?: LocalExtendedColors.current

    private var appColors: ExtendedColors? = null

    /**
     * Returns [light] if the system is in light theme, and [dark] otherwise.
     */
    @Composable
    fun <T> composableColor(light: T? = null, dark: T? = null): T? {
        val result = if (isSystemInDarkTheme()) dark else light
        if (result is ExtendedColors) {
            val current = appColors ?: LocalExtendedColors.current
            appColors = current.copy(
                customBackground = if (result.customBackground != Color.Unspecified) result.customBackground else current.customBackground,
                contentColor = if (result.contentColor != Color.Unspecified) result.contentColor else current.contentColor,
                autoBackground = if (result.autoBackground != Color.Unspecified) result.autoBackground else current.autoBackground,
                autoText = if (result.autoText != Color.Unspecified) result.autoText else current.autoText,
                themeColor = if (result.themeColor != Color.Unspecified) result.themeColor else current.themeColor
            )
        }
        return result
    }
}

private fun themeColor(isDark: Boolean, dark: Color, light: Color): Color {
    return if (isDark) dark else light
}


