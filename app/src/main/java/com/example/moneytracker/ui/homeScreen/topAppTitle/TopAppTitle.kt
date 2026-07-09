// =====
// Glory be to the name of LORD of hosts
// =====

package com.example.moneytracker.ui.homeScreen.topAppTitle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue

@Composable
fun TopAppTitle(
    state: PagerState,
    contentColor: Color,
    currentPageColor: Color,
    backgroundColor: Color,
    function: (TopBarNav) -> Unit
) {
    val topBarNav = remember { TopBarNav.entries }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .widthIn(min = 160.dp, max = 340.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        shape = RoundedCornerShape(50.dp),
        color = backgroundColor,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(2.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            topBarNav.forEachIndexed { index, nav ->
                TopBarItem(
                    index = index,
                    state = state,
                    nav = nav,
                    currentPageColor = currentPageColor,
                    contentColor = contentColor,
                    onClick = { function(nav) }
                )
            }
        }
    }
}

@Composable
private fun TopBarItem(
    index: Int,
    state: PagerState,
    nav: TopBarNav,
    currentPageColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    // Distance read is scoped to this sub-composable to avoid parent recomposition
    val distance = (state.currentPage + state.currentPageOffsetFraction - index).absoluteValue
    val activeProgress = (1f - distance).coerceIn(0f, 1f)

    val isSelected = activeProgress > 0.5f

    // Direct interpolation for perfectly synced color transitions during swipe
    val textColor = lerp(
        start = MaterialTheme.colorScheme.onSurfaceVariant,
        stop = contentColor,
        fraction = activeProgress
    )

    Box(
        modifier = Modifier
            .width(85.dp)
            .clip(RoundedCornerShape(50.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .background(currentPageColor.copy(alpha = activeProgress))
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = nav.text,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = 12.sp,
            color = textColor,
            modifier = Modifier.graphicsLayer {
                // Subtle scale effect for the active tab, synced with progress
                val scale = 1f + (0.05f * activeProgress)
                scaleX = scale
                scaleY = scale
            }
        )
    }
}
