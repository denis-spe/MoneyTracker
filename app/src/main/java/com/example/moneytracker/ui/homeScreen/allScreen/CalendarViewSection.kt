// Praise be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.helper.title
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarViewSection(
    color: Color = Color(0xFF2FA6B6),
    state: WeekCalendarState,
) {
    val currentMonth = YearMonth.now()
    val weekDays = remember { mutableStateOf<List<WeekDay>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val month = if (weekDays.value.isNotEmpty())
                weekDays.value.first().date.month.name else
                currentMonth.month.name


            Text(
                month.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

        }

        WeekCalendar(
            state = state,
            dayContent = { day ->
                val dayOfWeek = day.date.dayOfWeek.name.take(3)
                val dayOfMonth = day.date.dayOfMonth.toString()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(20))
                        .border(
                            1.dp,
                            color, RoundedCornerShape(20)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            dayOfWeek,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        dayOfMonth,
                        fontSize = 13.sp
                    )
                }
            },
            weekHeader = { week ->
                weekDays.value = week.days
            }
        )
    }
}