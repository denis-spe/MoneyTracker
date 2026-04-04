package com.example.moneytracker.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val customBackground: Color,
    val customContent: Color,
    val currentPage: Color,
    val autoBackground: Color,
    val autoText: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        customBackground = Color.Unspecified,
        customContent = Color.Unspecified,
        currentPage = Color.Unspecified,
        autoBackground = Color.Unspecified,
        autoText = Color.Unspecified
    )
}
