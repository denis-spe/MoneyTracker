// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind and
// you shall love your neighbour as yourself
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.dataAddition.FONT_WEIGHT
import com.example.moneytracker.ui.homeScreen.dataAddition.MaxWidth

@Composable
fun DatasetReceipt(
    dataset: Dataset
) {
    val datetime = dataset.dateTime.toLocalDateTimeUtc()
    val day = datetime.day.addZeroIfLessThenTen
    val month = datetime.month.name.title
    val year = datetime.year.addZeroIfLessThenTen
    val hour = datetime.hour.addZeroIfLessThenTen
    val minute = datetime.minute.addZeroIfLessThenTen
    val weekDay = datetime.dayOfWeek.name.title

    val time = "${hour}:${minute}"
    val date = "$day $month $year"


    Column(
        modifier = Modifier.fillMaxWidth(MaxWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = dataset.dataType.text, fontSize = 25.sp, fontWeight = FONT_WEIGHT)
        Text(text = "On ${weekDay}, $date", fontSize = 15.sp)
        Text(text = "At $time", fontSize = 15.sp)
        Text(buildString { repeat(40) { append("-") } })
    }
}

@Composable
fun AdjustmentReceipt(
    adjustment: Adjustment
) {

}


@Composable
fun Receipt(
    dataset: Dataset? = null,
    adjustment: Adjustment? = null,
    onShowDialog: MutableState<Boolean>
) {
    Dialog(
        onDismissRequest = {
            onShowDialog.value = false
        }
    ) {
        Card {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (dataset != null) {
                    DatasetReceipt(dataset = dataset)
                }
                if (adjustment != null) {
                    AdjustmentReceipt(adjustment = adjustment)
                }
            }
        }
    }
}

