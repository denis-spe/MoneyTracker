// =====
// Glory be to the name of LORD of hosts
// =====

package com.example.moneytracker.ui.homeScreen.topAppTitle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    // Use rememberUpdatedState to avoid reading frequently-changing values directly
    val pagerState by rememberUpdatedState(state)
    val selectedTabIndex = pagerState.currentPage

    Box(
        modifier = Modifier.background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier,
            containerColor = Color.Transparent,
            contentColor = contentColor,
            edgePadding = 16.dp
        ) {
            topBarNav.forEachIndexed { index, nav ->
                // Calculate distance using the pager state
                val distance =
                    (pagerState.currentPage + pagerState.currentPageOffsetFraction - index).absoluteValue
                val activeProgress = (1f - distance).coerceIn(0f, 1f)
                val isSelected = activeProgress > 0.5f

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) contentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(300),
                    label = "TextColorAnimation"
                )

                Tab(
                    selected = isSelected,
                    onClick = { function(nav) },
                    text = {
                        Text(
                            text = nav.text,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 12.sp,
                            color = textColor
                        )
                    }
                )
            }
        }
    }
}
