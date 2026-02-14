// Hear oh Israel, The LORD our GOD, The LORD is one,
// Thou shalt love the LORD your God with all your heart and soul and with all your mind,
// and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.allScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.datetime.toKotlinLocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllScreen(
    paddingValues: PaddingValues,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val currentMonth = YearMonth.now()
    val startDate = currentMonth.minusMonths(10).atDay(1) // 10 months ago
    val endDate = currentMonth.plusMonths(10).atEndOfMonth() // 10 months ahead
    val firstDayOfWeek = firstDayOfWeekFromLocale() // Helper or DayOfWeek.MONDAY

    val state = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstDayOfWeek = firstDayOfWeek
    )

    val weeklyData = viewModel.weeklyData(
        state.startDate.toKotlinLocalDate(),
        state.endDate.toKotlinLocalDate()
    ).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CalendarViewSection(state = state)
        ListOfData(data = weeklyData.value)
    }
}
