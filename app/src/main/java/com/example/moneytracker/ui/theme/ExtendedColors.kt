package com.example.moneytracker.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val customBackground: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
    val autoBackground: Color = Color.Unspecified,
    val autoText: Color = Color.Unspecified,
    val themeColor: Color = Color.Unspecified
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        customBackground = Color.Unspecified,
        contentColor = Color.Unspecified,
        autoBackground = Color.Unspecified,
        autoText = Color.Unspecified,
        themeColor = Color.Unspecified
    )
}
