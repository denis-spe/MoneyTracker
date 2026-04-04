// =====
// Glory be to the name of LORD of hosts
// =====
package com.example.moneytracker.ui.homeScreen.topAppTitle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

@Composable
fun TopAppTitle(
    state: State<HomeUiState>,
    contentColor: Color,
    currentPageColor: Color,
    backgroundColor: Color,
    function: (TopBarNav) -> Unit
) {

    val topBarNav = TopBarNav.entries

    // Text weight
    val fontWeight = FontWeight.Bold
    val fontSize = 13.sp

    var selectedTabIndex by remember {
        mutableIntStateOf(
            topBarNav
            .find { it == state.value.topTitle }?.ordinal ?: 0
        )
    }


    PrimaryScrollableTabRow(
        selectedTabIndex,
        modifier = Modifier
            .widthIn(min = 160.dp, max = 300.dp)
            .clip(RoundedCornerShape(50.dp)),
        containerColor = backgroundColor,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(selectedTabIndex, matchContentSize = true)
                    .clip(RoundedCornerShape(100))
                    .padding(bottom = 2.dp),
                width = 5.dp,
                height = 5.dp,
                color = MoneyTrackerTheme.colors.autoText
            )
        },
        divider = {},
    ) {
        topBarNav.forEachIndexed { index, nav ->
            val isSelected = state.value.topTitle == nav && selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = {
                    selectedTabIndex = index
                    function(nav)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected)
                            currentPageColor else
                            Color.Unspecified
                    ),
                unselectedContentColor = MoneyTrackerTheme.colors.autoText,
                selectedContentColor = contentColor
            ) {
                Text(
                    nav.text,
                    fontWeight = fontWeight,
                    fontSize = fontSize,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}