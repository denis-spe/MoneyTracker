// Great is the LORD of host.
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.ui.theme.autoColorChange
import com.example.moneytracker.ui.theme.autoTextColorChange
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.MAX
import network.chaintech.kmp_date_time_picker.utils.MIN
import network.chaintech.kmp_date_time_picker.utils.TimeFormat
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults
import network.chaintech.kmp_date_time_picker.utils.now

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChainNetworkDateTime(
    showDateTime: MutableState<Boolean>,
    localDateTimeState: MutableState<LocalDateTime>
) {
    WheelDateTimePickerView(
        modifier = Modifier,
        showDatePicker = showDateTime.value,
        title = "Pick a date and time",
        doneLabel = "Done",
        timeFormat = TimeFormat.HOUR_24,
        titleStyle = LocalTextStyle.current.copy(
            color = Color.autoTextColorChange,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        doneLabelStyle = LocalTextStyle.current,
        startDate = LocalDateTime.now(),
        minDate = LocalDateTime.MIN(),
        maxDate = LocalDateTime.MAX(),
        yearsRange = IntRange(1922, 2122),
        height = 300.dp,
        rowCount = 3,
        selectedDateTextStyle = MaterialTheme.typography.titleMedium.copy(
            color = Color.autoTextColorChange,
            fontSize = 18.sp
        ),
        defaultDateTextStyle = MaterialTheme.typography.titleSmall.copy(
            color = Color.autoTextColorChange,
            fontSize = 16.sp
        ),
        hideHeader = false,
        showMonthAsNumber = false,
        customMonthNames = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        ),
        containerColor = Color.autoColorChange,
        shape = RoundedCornerShape(10.dp),
        dateTimePickerView = DateTimePickerView.DIALOG_VIEW,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        selectorProperties = WheelPickerDefaults.selectorProperties(),
        onDoneClick = { localDateTime ->
            // Handle the selected date and time
            localDateTimeState.value = localDateTime
            showDateTime.value = false
        },
        onDateChangeListener = {},
        onDismiss = {
            showDateTime.value = false
        },
    )
}


@Composable
fun ChainNetworkDateTimeButton(
    showDateTime: MutableState<Boolean>,
    localDateTimeState: MutableState<LocalDateTime>,
    colorResId: Int
) {
    val date = localDateTimeState.value.date
    val time = localDateTimeState.value.time
    val hour = if (time.hour < 10) "0${time.hour}" else time.hour
    val minute = if (time.minute < 10) "0${time.minute}" else time.minute
    val dayOfWeek = localDateTimeState.value.dayOfWeek.name
    val color = colorResource(colorResId)

    OutlinedButton(
        onClick = { showDateTime.value = true },
        colors = ButtonDefaults.outlinedButtonColors().copy(
            contentColor = color,
            containerColor = color.copy(alpha = 0.2f),
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .size(25.dp)
                    .padding(end = 7.dp),
                painter = painterResource(id = R.drawable.calendar_and_clock),
                contentDescription = "Calendar and clock"
            )
            Text(
                "$dayOfWeek $date ${hour}:${minute}",
                fontWeight = FONT_WEIGHT
            )
        }
    }

    // Show the date time picker wheel.
    ChainNetworkDateTime(showDateTime, localDateTimeState)
}

