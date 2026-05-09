// Praise be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneytracker.helper.getWeeks
import com.example.moneytracker.helper.title
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import kotlinx.coroutines.flow.map
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.LocalDate
import java.time.temporal.IsoFields

@Composable
fun CalendarViewSection(
    updateWeek: (dates: List<kotlinx.datetime.LocalDate>) -> Unit,
    viewModel: HomeViewModel
) {
    val now = kotlinx.datetime.LocalDate.now()
    val currentWeek by viewModel.currentWeekDerived.collectAsStateWithLifecycle()
    val fontSizeMonthDay = 13.sp
    val date by viewModel.currentDateDerived.collectAsStateWithLifecycle()
    val activityCounts by viewModel.activityCounts.collectAsStateWithLifecycle()

    val month = remember(date) { date.month.name.title }
    val year = remember(date) { date.year.toString() }
    val weekNumber =
        remember(date) { date.toJavaLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) }
    val selectedTabIndex by remember(viewModel.uiState) {
        viewModel.uiState.map { it.selectedTabIndex }
    }.collectAsStateWithLifecycle(1)
    var selectedDate by remember { mutableStateOf(date) }
    val selectedColor = MoneyTrackerTheme.colors.themeColor
    val contentColor = MoneyTrackerTheme.colors.contentColor


    // Change the tab index to week on page swipe
    LaunchedEffect(weekNumber) {
        viewModel.updateSelectedTabIndex(1)
    }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            contentColor = contentColor,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex,
                        matchContentSize = true
                    ),
                    width = Dp.Unspecified,
                    color = selectedColor
                )
            },
            divider = {
                HorizontalDivider(
                    color = MoneyTrackerTheme.colors.currentPage
                )
            },
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = {
                    viewModel.updateSelectedTabIndex(0)
                },
                selectedContentColor = selectedColor,
                unselectedContentColor = contentColor
            ) {
                val day = selectedDate.let { "Day ${it.day}" }

                Text(
                    text = day,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSizeMonthDay,
                )
            }

            Tab(
                selected = selectedTabIndex == 1,
                onClick = {
                    viewModel.updateSelectedTabIndex(1)
                },
                selectedContentColor = selectedColor,
                unselectedContentColor = contentColor
            ) {
                Text(
                    text = "Week $weekNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSizeMonthDay,
                )
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    month,
                    fontSize = fontSizeMonthDay,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    year,
                    fontSize = fontSizeMonthDay,
                    fontWeight = FontWeight.Bold
                )
            }
            GroupedWeeks { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    week.forEach { date ->
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    viewModel.updateSelectedTabIndex(0)
                                    selectedDate = date
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                date.dayOfWeek.name.title.take(3),
                                fontSize = fontSizeMonthDay,
                                fontWeight = FontWeight.Medium,
                                color = if (date == now) selectedColor else Color.Unspecified
                            )

                            if (activityCounts is DataState.Success) {
                                BadgedBox(
                                    badge = {
                                        val lenOfAct = (activityCounts as
                                                DataState.Success<Map<kotlinx.datetime.LocalDate, Int>>)
                                            .data[date] ?: 0
                                        if (lenOfAct > 0) {
                                            Badge(
                                                containerColor = selectedColor,
                                            ) {
                                                Text(lenOfAct.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(30.dp)
                                            .border(
                                                width = 1.dp,
                                                color = if (date == selectedDate && selectedTabIndex == 0)
                                                    selectedColor else Color.Transparent,
                                                shape = RoundedCornerShape(100)
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            date.day.toString(),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (date == now) selectedColor else
                                                contentColor
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(30.dp)
                                        .border(
                                            width = 1.dp,
                                            color = if (date == selectedDate && selectedTabIndex == 0)
                                                selectedColor else Color.Transparent,
                                            shape = RoundedCornerShape(100)
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        date.day.toString(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (date == now) selectedColor else
                                            contentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedTabIndex, selectedDate, currentWeek) {
        when (selectedTabIndex) {
            0 -> updateWeek(listOf(selectedDate))
            1 -> updateWeek(currentWeek)
        }
    }
}


@Composable
fun GroupedWeeks(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    weeksAfter: Int = 100,
    weeksBefore: Int = 100,
    moveTo: (initialPage: Int, pageState: PagerState) -> Unit = { _, _ -> },
    weekView: @Composable (week: List<kotlinx.datetime.LocalDate>) -> Unit
) {

    val localDateList = remember(weeksBefore, weeksAfter) {
        val currentDate = LocalDate.now()
        getWeeks(anchorDate = currentDate, weeksBefore, weeksAfter)
    }

    val initialIndex = remember(localDateList) {
        val currentDate = LocalDate.now()
        localDateList.indexOfFirst { it.contains(currentDate) }.coerceAtLeast(0)
    }

    val pageState = rememberPagerState(initialPage = initialIndex) { localDateList.size }

    LaunchedEffect(pageState.currentPage) {
        viewModel.updateCurrentWeek(localDateList[pageState.currentPage].map { it.toKotlinLocalDate() })
    }


    HorizontalPager(
        state = pageState,
        modifier = modifier,
        key = { localDateList[it] }
    ) {
        val week = remember(it, localDateList) {
            localDateList[it].map { weekValue -> weekValue.toKotlinLocalDate() }
        }
        weekView(week)
    }

    moveTo(initialIndex, pageState)
}



