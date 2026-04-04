// Praise be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
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
import com.example.moneytracker.helper.getWeeks
import com.example.moneytracker.helper.title
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.LocalDate
import java.time.temporal.IsoFields

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarViewSection(
    updateWeek: (dates: List<kotlinx.datetime.LocalDate>) -> Unit,
    viewModel: HomeScreenViewModel
) {
    val uiState = viewModel.uiState.collectAsState()
    val now = kotlinx.datetime.LocalDate.now()
    val currentWeek = viewModel.getCurrentWeek.collectAsState(initial = emptyList())
    val fontSizeMonthDay = 13.sp
    val date = viewModel.getAllCurrentDate.collectAsState(
        initial = now
    )
    val month = date.value.month.name.title
    val year = date.value.year.toString()
    val weekNumber = date.value.toJavaLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
    val selectedTabIndex = uiState.value.selectedTabIndex
    var selectedDate by remember { mutableStateOf(date.value) }
    val selectedColor = MoneyTrackerTheme.colors.autoText
    val contentColor = MoneyTrackerTheme.colors.autoText.copy(0.3f)



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
                    color = contentColor
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

                            BadgedBox(
                                badge = {
                                    val lenOfAct = viewModel.getLenOfActivates(date)
                                    if (lenOfAct > 0) {
                                        Badge(
                                            containerColor = selectedColor,
                                            contentColor = Color.White
                                        ) {
                                            Text(lenOfAct.toString())
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    date.day.toString(),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = if (date == selectedDate && selectedTabIndex == 0)
                                                selectedColor else Color.Transparent,
                                            shape = RoundedCornerShape(10)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 5.dp),
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

    LaunchedEffect(selectedTabIndex, selectedDate, currentWeek.value) {
        when (selectedTabIndex) {
            0 -> updateWeek(listOf(selectedDate))
            1 -> updateWeek(currentWeek.value)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupedWeeks(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    weeksAfter: Int = 100,
    weeksBefore: Int = 100,
    moveTo: (initialPage: Int, pageState: PagerState) -> Unit = { _, _ -> },
    weekView: @Composable (week: List<kotlinx.datetime.LocalDate>) -> Unit
) {

    val currentDate = LocalDate.now()
    val localDateList = remember {
        getWeeks(anchorDate = currentDate, weeksBefore, weeksAfter)
    }

    val getIndexOfCurrentWeek = remember(localDateList) {
        localDateList.indexOfFirst { it.contains(currentDate) }
    }


    val pageState = rememberPagerState(initialPage = getIndexOfCurrentWeek) { localDateList.size }

    LaunchedEffect(pageState.currentPage) {
        viewModel.updateCurrentWeek(localDateList[pageState.currentPage])
    }


    HorizontalPager(
        state = pageState,
        modifier = modifier,
        key = { localDateList[it] }
    ) {
        val week = localDateList[it].map { weekValue -> weekValue.toKotlinLocalDate() }
        weekView(week)
    }

    moveTo(getIndexOfCurrentWeek, pageState)
}


