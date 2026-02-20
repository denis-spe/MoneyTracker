// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind, and
// you shall love your neighbor as yourself
package com.example.moneytracker.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.isOverdue

@Composable
fun StatusView(dataAdjust: DataAdjust) {

    val isOverdue = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.isOverdue
        is DataAdjust.Adjust -> dataAdjust.adjustment.dataset?.isOverdue
    }

    when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.isAmountEqualToAdjustAmount()
        is DataAdjust.Adjust -> dataAdjust.adjustment.dataset?.isAmountEqualToAdjustAmount()
    }

    when (isOverdue) {
        Status.PENDING -> Text(
            Status.PENDING.text,
            color = colorResource(id = Status.PENDING.color)
        )

        Status.FAILED -> Text(
            Status.FAILED.text,
            color = colorResource(id = Status.FAILED.color)
        )

        Status.SUCCESS -> Text(
            Status.SUCCESS.text,
            color = colorResource(id = Status.SUCCESS.color)
        )

        else -> {}
    }
}

