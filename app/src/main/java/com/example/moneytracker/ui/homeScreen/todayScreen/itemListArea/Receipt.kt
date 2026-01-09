// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind and
// you shall love your neighbour as yourself
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.runtime.Composable
import com.example.moneytracker.backend.storage.DataType
import com.google.firebase.Timestamp

@Composable
fun Receipt(
    label: String,
    labelIcon: Int,
    amount: Double,
    dataType: DataType,
    colorResId: Int,
    description: String,
    dateTime: Timestamp
) {

}

