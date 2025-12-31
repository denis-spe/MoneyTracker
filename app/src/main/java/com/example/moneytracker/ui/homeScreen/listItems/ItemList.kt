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
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE

@Composable
fun ItemList(datasets: List<Dataset>) {
    LazyColumn(
        modifier = Modifier.height(200.dp)
    ) {

        items(datasets.size) {
            val dataset = datasets[it]
            ItemCard(dataset)
        }
    }
}

@Composable
fun ItemCard(dataset: Dataset) {
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
                val day = dateTime.day
                val hour = if (dateTime.hour < 10) "0${dateTime.hour}" else dateTime.hour
                val minute = if (dateTime.minute < 10) "0${dateTime.minute}" else dateTime.minute
                val month = dateTime.month.name.take(3).lowercase().mapIndexedNotNull { index, c ->
                    if (index == 0) c.uppercase() else c.lowercase()
                }.joinToString("")
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