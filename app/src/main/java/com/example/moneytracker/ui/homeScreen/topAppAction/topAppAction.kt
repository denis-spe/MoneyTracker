// Glory be to the Lord of hosts
package com.example.moneytracker.ui.homeScreen.topAppAction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun TopAppAction() {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) { }
    IconButton(
        onClick = { /*TODO*/ }
    ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
    }

    IconButton(
        onClick = { /*TODO*/ }
    ) {
        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Search")
    }
}
