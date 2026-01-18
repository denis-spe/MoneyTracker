// Great is the LORD of host.
package com.example.moneytracker.ui.homeScreen.dataAddition

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneytracker.R
import com.example.moneytracker.helper.addZeroIfLessThenTen
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.number
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    onDateSelected: (DatePickerState) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = LocalDate.now(),
        initialDisplayMode = DisplayMode.Input,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComponent(
    title: String,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit
) {

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    /** Determines whether the time picker is dial or input */
    var showDial by remember { mutableStateOf(false) }

    /** The icon used for the icon button that switches from dial to input */
    val toggleIcon = if (showDial) {
        Icons.Filled.EditCalendar
    } else {
        Icons.Filled.AccessTime
    }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                if (showDial) {
                    TimePicker(
                        state = timePickerState,
                    )
                } else {
                    TimeInput(
                        state = timePickerState,
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { showDial = !showDial }) {
                        Icon(
                            imageVector = toggleIcon,
                            contentDescription = "Time picker type toggle",
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = { onConfirm(timePickerState) }) { Text("OK") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoubleTimePickerComponent(
    onConfirm: (TimePickerState, TimePickerState) -> Unit,
    onDismiss: () -> Unit
) {

    val currentTime = Calendar.getInstance()

    val firstTimePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    val secondTimePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Your starting time",
                    style = MaterialTheme.typography.labelMedium
                )

                TimeInput(
                    state = firstTimePickerState,
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Your ending time",
                    style = MaterialTheme.typography.labelMedium
                )

                TimeInput(
                    state = secondTimePickerState,
                )

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        onConfirm(
                            firstTimePickerState,
                            secondTimePickerState
                        )
                    }) { Text("OK") }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeInput(
    showTime: MutableState<Boolean>,
    showDate: MutableState<Boolean>,
    localDateTimeState: MutableState<LocalDateTime>,
    colorResId: Int
) {
    val date = localDateTimeState.value.date
    val time = localDateTimeState.value.time
    val hour = if (time.hour < 10) "0${time.hour}" else time.hour
    val minute = if (time.minute < 10) "0${time.minute}" else time.minute
    val dayOfWeek = localDateTimeState.value.dayOfWeek.name
    val color = colorResource(colorResId)
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    val padding = integerResource(R.integer.modelDrawerPadding).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp

    Row(
        modifier = Modifier
            .height(height)
            .fillMaxWidth(MaxWidth)
            .padding(bottom = padding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.border(1.dp, color, CircleShape),
            onClick = { showDate.value = true }
        ) {
            Image(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendar",
                modifier = Modifier.size(ICON_SIZE)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${dayOfWeek.take(3)}, $date",
                fontSize = fontSize,
                color = color
            )
            Text(
                text = "$hour:$minute",
                fontSize = fontSize,
                color = color
            )
        }

        IconButton(
            modifier = Modifier.border(1.dp, color, CircleShape),
            onClick = { showTime.value = true }
        ) {
            Image(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Clock",
                modifier = Modifier.size(ICON_SIZE)
            )
        }
    }


    // Show the date picker.
    if (showDate.value) {
        DatePickerComponent({ localDate ->
            localDate.getSelectedDate()?.let {
                // Change the date to kotlin date
                val localDate = it.toKotlinLocalDate()

                // Add the time to the date
                val localDateTime = localDate.atTime(localDateTimeState.value.time)

                // Update the state
                localDateTimeState.value = localDateTime

                // Close the date picker
                showDate.value = false
            }
        }) {
            showDate.value = false
        }
    }

    // Time picker
    if (showTime.value) {
        TimePickerComponent(
            "Select time",
            onConfirm = { timePickerState ->
                // Hour and minute from time picker
                val hour = timePickerState.hour
                val minute = timePickerState.minute

                // Add the time to the date
                val localDateTime = localDateTimeState.value.date.atTime(hour, minute)

                // Update the state
                localDateTimeState.value = localDateTime

                // Close the time picker
                showTime.value = false
            }) {
            showTime.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimeRange(
    startLocalDateTimeState: MutableState<LocalDateTime>,
    endLocalDateTimeState: MutableState<LocalDateTime>,
    colorResId: Int
) {
    val isPresentStartDateDialogOpen = remember { mutableStateOf(false) }
    val isPresentEndDateDialogOpen = remember { mutableStateOf(false) }
    val isTimeDialogOpen = remember { mutableStateOf(false) }

    val startDate = startLocalDateTimeState.value.date
    val day = startDate.day.addZeroIfLessThenTen
    val month = startDate.month.number.addZeroIfLessThenTen
    val year = startDate.year

    val endDate = endLocalDateTimeState.value.date
    val endingDay = endDate.day.addZeroIfLessThenTen
    val endingMonth = endDate.month.number.addZeroIfLessThenTen
    val endingYear = endDate.year

    Row(
        modifier = Modifier
            .fillMaxWidth(MaxWidth)
            .padding(bottom = integerResource(R.integer.modelDrawerPadding).dp)

    ) {
        val shape = RoundedCornerShape(10.dp)
        colorResource(colorResId)

        Column {
            IconButton(
                modifier = Modifier.border(1.dp, Color.Gray, CircleShape),
                onClick = { isTimeDialogOpen.value = true }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(ICON_SIZE)
                )
            }
        }

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable { isPresentStartDateDialogOpen.value = true },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Start date")
                Text("$year/$month/$day")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable { isPresentEndDateDialogOpen.value = true },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("End date")
                Text("$endingYear/$endingMonth/$endingDay")
            }
        }

        if (isPresentStartDateDialogOpen.value) {
            DatePickerComponent({ localDate ->
                localDate.getSelectedDate()?.let {
                    // Change the date to kotlin date
                    val localDate = it.toKotlinLocalDate()

                    // Add the time to the date
                    val localDateTime = localDate.atTime(startLocalDateTimeState.value.time)

                    // Update the state
                    startLocalDateTimeState.value = localDateTime

                    // Close the date picker
                    isPresentStartDateDialogOpen.value = false
                }
            }) {
                isPresentStartDateDialogOpen.value = false
            }
        }

        if (isPresentEndDateDialogOpen.value) {
            DatePickerComponent({ localDate ->
                localDate.getSelectedDate()?.let {
                    // Change the date to kotlin date
                    val localDate = it.toKotlinLocalDate()

                    // Add the time to the date
                    val localDateTime = localDate.atTime(endLocalDateTimeState.value.time)

                    // Update the state
                    endLocalDateTimeState.value = localDateTime

                    // Close the date picker
                    isPresentEndDateDialogOpen.value = false

                }
            }) {
                isPresentEndDateDialogOpen.value = false
            }
        }
    }

    if (isTimeDialogOpen.value) {
        DoubleTimePickerComponent(
            onConfirm = { firstTimePickerState, secondTimePickerState ->
                // Hour and minute from time picker
                val firstHour = firstTimePickerState.hour
                val firstMinute = firstTimePickerState.minute
                val secondHour = secondTimePickerState.hour
                val secondMinute = secondTimePickerState.minute

                // Add the time to the date
                val startLocalDateTime = startLocalDateTimeState.value.date.atTime(
                    firstHour, firstMinute
                )
                val endLocalDateTime = endLocalDateTimeState.value.date.atTime(
                    secondHour, secondMinute
                )


                // Update the state
                startLocalDateTimeState.value = startLocalDateTime
                endLocalDateTimeState.value = endLocalDateTime

                // Close the time picker
                isTimeDialogOpen.value = false
            }
        ) {
            isTimeDialogOpen.value = false
        }
    }

}

