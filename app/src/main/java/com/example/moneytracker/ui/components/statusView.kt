// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind, and
// you shall love your neighbor as yourself
package com.example.moneytracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.status
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

@Composable
fun StatusView(
    dataAdjust: DataAdjust,
    showImageStatus: Boolean = false,
    imageSize: Dp = 16.dp,
    fontSize: TextUnit = TextUnit.Unspecified
) {

    val status = remember {
        mutableStateOf(Status.INITIAL)
    }

    val now = remember {
        mutableStateOf(LocalDateTime.now())
    }

    // Update 'now' every second to track time changes
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Update every second
            now.value = LocalDateTime.now()
        }
    }

    when (dataAdjust) {
        is DataAdjust.Adjust ->
            LaunchedEffect(
                dataAdjust.adjustment,
                dataAdjust.adjustment.finance,
                now.value
            ) {
                status.value = when (dataAdjust) {
                    is DataAdjust.Adjust -> dataAdjust.adjustment.finance?.status!!
                }
            }

        is DataAdjust.Data -> {
            LaunchedEffect(dataAdjust.finance, now.value) {
                status.value = when (dataAdjust) {
                    is DataAdjust.Data -> dataAdjust.finance.status
                }
            }
        }
    }



    when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.finance.isAmountEqualToAdjustAmount()
        is DataAdjust.Adjust -> dataAdjust.adjustment.finance?.isAmountEqualToAdjustAmount()
    }

    if (status.value == Status.INITIAL) {
        return
    }

    if (showImageStatus) {
        Image(
            painter = painterResource(id = status.value.icon),
            contentDescription = status.value.text,
            modifier = Modifier.size(imageSize),
        )
    } else {
        Text(
            status.value.text,
            color = colorResource(id = status.value.color),
            fontSize = fontSize
        )
    }
}

