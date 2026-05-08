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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

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

    // IMPORTANT:
    // settledPage updates ONLY after animation completes
    // much smoother than currentPage
    val selectedTabIndex by remember {
        derivedStateOf {
            state.settledPage
        }
    }

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

                color = MoneyTrackerTheme.colors.autoText
            )
        },

        divider = {}

    ) {

        topBarNav.forEachIndexed { index, nav ->

            TopBarItem(
                nav = nav,

                selected = selectedTabIndex == index,

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
    selected: Boolean,
    currentPageColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {

    Tab(
        selected = selected,

        onClick = onClick,

        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (selected)
                    currentPageColor
                else
                    Color.Transparent
            ),

        unselectedContentColor = MoneyTrackerTheme.colors.autoText,

        selectedContentColor = contentColor
    ) {

        Text(
            text = nav.text,

            fontWeight = FontWeight.Bold,

            fontSize = 13.sp,

            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}