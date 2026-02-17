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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.helper.title
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarViewSection(
    color: Color = Color(0xFF2FA6B6),
    updateWeek: (dates: List<kotlinx.datetime.LocalDate>) -> Unit
) {

    val currentWeek = remember { mutableStateOf(emptyList<kotlinx.datetime.LocalDate>()) }
    val fontSizeMonthDay = 13.sp
    val date = if (currentWeek.value.isEmpty())
        kotlinx.datetime.LocalDate.now()
    else currentWeek.value.first()
    val month = date.month.name.title
    val year = date.year.toString()
    val weekNumber = date.toJavaLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    var selectedDate by remember { mutableStateOf(date) }

    // Change the tab index to week on page swipe
    LaunchedEffect(weekNumber) {
        selectedTabIndex = 1
    }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = {
                    selectedTabIndex = 0
                }
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
                    selectedTabIndex = 1
                }
            ) {
                Text(
                    text = "Week $weekNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSizeMonthDay,
                )
            }

            Tab(
                selected = selectedTabIndex == 2,
                onClick = {
                    selectedTabIndex = 2
                }
            ) {
                Text(
                    month,
                    fontSize = fontSizeMonthDay,
                    fontWeight = FontWeight.Bold
                )
            }

            Tab(
                selected = selectedTabIndex == 3,
                onClick = {
                    selectedTabIndex = 3
                }
            ) {
                Text(
                    year,
                    fontSize = fontSizeMonthDay,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupedWeeks(
                currentWeek = currentWeek,
            ) { week ->
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
                                    selectedTabIndex = 0
                                    selectedDate = date
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                date.dayOfWeek.name.title.take(3),
                                fontSize = fontSizeMonthDay,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                date.day.toString(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (date == selectedDate) color else Color.Transparent,
                                        shape = RoundedCornerShape(10)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Update the week when the currentWeek changes
    when (selectedTabIndex) {
        0 -> updateWeek(listOf(selectedDate))
        1 -> updateWeek(currentWeek.value)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupedWeeks(
    modifier: Modifier = Modifier,
    currentWeek: MutableState<List<kotlinx.datetime.LocalDate>>,
    weeksAfter: Int = 100,
    weeksBefore: Int = 100,
    weekView: @Composable (week: List<kotlinx.datetime.LocalDate>) -> Unit
) {

    val currentDate = LocalDate.now()
    val localDateList = getWeeks(
        anchorDate = currentDate,
        weeksAfter = weeksAfter,
        weeksBefore = weeksBefore
    )

    val getIndexOfCurrentWeek = localDateList.indexOfFirst {
        it.contains(currentDate)
    }

    val pageState = rememberPagerState(initialPage = getIndexOfCurrentWeek) { localDateList.size }
    currentWeek.value = localDateList[pageState.currentPage].map { it.toKotlinLocalDate() }


    HorizontalPager(
        state = pageState,
        modifier = modifier,
        key = { localDateList[it] }
    ) {
        val week = localDateList[it].map { weekValue -> weekValue.toKotlinLocalDate() }
        weekView(week)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getWeeks(
    anchorDate: LocalDate,
    weeksBefore: Int,
    weeksAfter: Int
): List<List<LocalDate>> {
    val weeks = mutableListOf<List<LocalDate>>()

    // 1. Find the start of the week for the anchor date
    val anchorWeekStart = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    // 2. Move the pointer back by the number of 'weeksBefore'
    var currentStart = anchorWeekStart.minusWeeks(weeksBefore.toLong())

    // 3. Loop through total count (Before + Current + After)
    val totalWeeks = weeksBefore + 1 + weeksAfter

    repeat(totalWeeks) {
        val week = (0..6).map { currentStart.plusDays(it.toLong()) }
        weeks.add(week)
        currentStart = currentStart.plusWeeks(1)
    }

    return weeks
}

