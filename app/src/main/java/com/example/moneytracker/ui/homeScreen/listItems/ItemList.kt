// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.listItems

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Repay
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE

val Int.addZeroIfLessThenTen: String
    get() = if (this < 10) "0$this" else this.toString()

@Composable
fun ItemList(datasets: List<Dataset>) {

    LazyColumn(
        modifier = Modifier.height(200.dp)
    ) {
        items(datasets.size) {
            val dataset = datasets[it]
            ItemCardDataset(dataset)
            val repayments = dataset.repay
            if (repayments.isNotEmpty()) {
                for (repay in repayments)
                    ItemCardRepay(repay)
            }
        }


//        if (repay.isNotEmpty()) {
//            items(repay.size) {
//                val repayData = repay[it]
//                ItemCardDataset(
//                    Dataset(
//                        id = repayData.repayId,
//                        dataType = DataType.REPAY,
//                        amount = repayData.amount,
//                        label = repayData.label,
//                        description = repayData.description,
//                        dateTime = repayData.dateTime,
//                        labelIcon = repayData.labelIcon
//                    )
//                )
//            }
//        }
    }
}

@Composable
fun ItemCardDataset(dataset: Dataset) {
    val color = colorResource(dataset.dataType.color)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            // Icon column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(dataset.labelIcon),
                    contentDescription = "list icon",
                    modifier = Modifier.size(ICON_SIZE)
                )
            }

            // Label, description, and date time column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                var description = dataset.description
                if (description.length > 10)
                    description = description.substring(0..10) + "..."

                val dateTime = dataset.dateTime.toLocalDateTimeUtc()
                val day = dateTime.day.addZeroIfLessThenTen
                val hour = dateTime.hour.addZeroIfLessThenTen
                val minute = dateTime.minute.addZeroIfLessThenTen
                val month = dateTime.month.name.take(3).title
                val year = dateTime.year
                val dateTimeAsString = "$day $month $year, $hour:$minute"

                Text(text = dataset.label)
                if (dataset.description.isNotEmpty()) {
                    Text(text = description)
                }
                Text(text = dateTimeAsString)
            }

            // Amount column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                var amount = dataset.amount.formatToAmount()
                amount = when (dataset.dataType) {
                    DataType.EXPENSE -> "-$amount"
                    DataType.DEBT -> "-$amount"
                    else -> amount
                }

                Text(
                    text = amount,
                    color = color
                )
            }

        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(top = 5.dp),
            color = color,
            thickness = 1.dp
        )
    }
}

@Composable
fun ItemCardRepay(dataset: Repay) {
    val color = colorResource(R.color.Repay)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            // Icon column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(dataset.labelIcon),
                    contentDescription = "list icon",
                    modifier = Modifier.size(ICON_SIZE)
                )
            }

            // Label, description, and date time column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                var description = dataset.description
                if (description.length > 10)
                    description = description.substring(0..10) + "..."

                val dateTime = dataset.dateTime.toLocalDateTimeUtc()
                val day = dateTime.day.addZeroIfLessThenTen
                val hour = dateTime.hour.addZeroIfLessThenTen
                val minute = dateTime.minute.addZeroIfLessThenTen
                val month = dateTime.month.name.take(3).title
                val year = dateTime.year
                val dateTimeAsString = "$day $month $year, $hour:$minute"

                Text(text = dataset.label)
                if (dataset.description.isNotEmpty()) {
                    Text(text = description)
                }
                Text(text = dateTimeAsString)
            }

            // Amount column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                val amount = dataset.amount.formatToAmount()

                Text(
                    text = amount,
                    color = color
                )
            }

        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(top = 5.dp),
            color = color,
            thickness = 1.dp
        )
    }
}