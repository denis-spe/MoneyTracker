// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.formatToAmount


@Composable
fun YesterdayStatArea(
    datasets: List<Dataset>,
) {
    val earnings = datasets.filter { it.dataType == DataType.EARNINGS }.sumOf { it.amount }
    val expenses = datasets.filter { it.dataType == DataType.EXPENSE }.sumOf { it.amount }
    val debts = datasets.filter { it.dataType == DataType.DEBT }.sumOf { it.amount }
    val lent = datasets.filter { it.dataType == DataType.LENT }.sumOf { it.amount }
    val savings = datasets.filter { it.dataType === DataType.SAVINGS }.sumOf { it.amount }
    datasets.filter { it.dataType == DataType.GOAL }.sumOf { it.amount }
    val attained = datasets.filter { it.dataType == DataType.GOAL }
        .map { it.adjustment }
        .flatten()
        .sumOf { it.amount }
    datasets.filter { it.dataType == DataType.LENT }
        .map { it.adjustment }
        .flatten()
        .sumOf { it.amount }

    datasets.filter { it.dataType == DataType.DEBT }
        .map { it.adjustment }
        .flatten()
        .sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(text = "Stats", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Earned:")
            Text(earnings.formatToAmount())
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Expenses:")
            Text(expenses.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Debts:")
            Text(debts.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Lent:")
            Text(lent.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Savings:")
            Text(savings.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Attained:")
            Text(attained.formatToAmount())
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reminder:")
            val reminder = (earnings - expenses) - (debts + lent + savings + attained)
            Text(reminder.formatToAmount())
        }
    }
}