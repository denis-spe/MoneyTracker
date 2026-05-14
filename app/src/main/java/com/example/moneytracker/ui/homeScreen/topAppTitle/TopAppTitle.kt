// =====
// Glory be to the name of LORD of hosts
// =====

package com.example.moneytracker.ui.homeScreen.topAppTitle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.ui.theme.StewardTheme
import kotlin.math.absoluteValue

@Composable
fun TopAppTitle(
    state: PagerState,
    contentColor: Color,
    currentPageColor: Color,
    backgroundColor: Color,
    function: (TopBarNav) -> Unit
) {

    val topBarNav = remember {
        TopBarNav.entries
    }

    val selectedTabIndex = state.currentPage

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,

        modifier = Modifier
            .widthIn(min = 160.dp, max = 300.dp)
            .clip(RoundedCornerShape(50.dp)),

        containerColor = backgroundColor,

        edgePadding = 0.dp,

        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(
                        selectedTabIndex = selectedTabIndex,
                        matchContentSize = true
                    )
                    .clip(RoundedCornerShape(100.dp))
                    .padding(bottom = 2.dp),

                width = 5.dp,
                height = 5.dp,

                color = contentColor
            )
        },

        divider = {}

    ) {

        topBarNav.forEachIndexed { index, nav ->
            val distance =
                (state.currentPage + state.currentPageOffsetFraction - index).absoluteValue
            val activeProgress = (1f - distance).coerceIn(0f, 1f)

            TopBarItem(
                nav = nav,

                activeProgress = activeProgress,

                currentPageColor = currentPageColor,

                contentColor = contentColor,

                onClick = {
                    function(nav)
                }
            )
        }
    }
}

@Composable
private fun TopBarItem(
    nav: TopBarNav,
    activeProgress: Float,
    currentPageColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {

    Tab(
        selected = activeProgress > 0.5f,

        onClick = onClick,

        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
                currentPageColor.copy(alpha = activeProgress)
            ),

        unselectedContentColor = StewardTheme.colors.onSurfaceText,

        selectedContentColor = lerp(
            StewardTheme.colors.onSurfaceText,
            contentColor,
            activeProgress
        )
    ) {

        Text(
            text = nav.text,

            fontWeight = FontWeight.Bold,

            fontSize = 13.sp,

            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}