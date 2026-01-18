// Bless be the name of LORD our GOD
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class AdjustmentType(
    val text: String,
    val colorDebt: Int = R.color.RepayDebt,
    val colorLent: Int = R.color.RepayLoan,
    val colorAttain: Int = R.color.Attain,
    val icon: Int,
) {
    REPAYMENT("Repay", icon = R.drawable.outlined_repay),
    ATTAIN("Attain", icon = R.drawable.oulined_attain),
}