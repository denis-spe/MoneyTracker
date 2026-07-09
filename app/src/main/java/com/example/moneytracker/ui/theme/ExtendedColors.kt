package com.example.moneytracker.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val secondarySurface: Color = Color.Unspecified,
    val accentContent: Color = Color.Unspecified,
    val onSurfaceText: Color = Color.Unspecified,
    val primary: Color = Color.Unspecified
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        secondarySurface = Color.Unspecified,
        accentContent = Color.Unspecified,
        onSurfaceText = Color.Unspecified,
        primary = Color.Unspecified
    )
}
