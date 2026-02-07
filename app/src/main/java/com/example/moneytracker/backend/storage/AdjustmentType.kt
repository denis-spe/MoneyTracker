// Bless be the name of LORD our GOD
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class AdjustmentType(
    val text: String,
    val color: Int,
    val icon: Int,
) {
    DEBT_REPAY("Payback", icon = R.drawable.outlined_repay, color = R.color.RepayDebt),
    LENT_REPAY("Loan Refund", icon = R.drawable.outlined_repay, color = R.color.RepayLoan),
    GOAL_ATTAIN("Goal Attain", icon = R.drawable.oulined_attain, color = R.color.Attain),
    INITIAL("Initial", icon = R.drawable.outlined_repay, color = R.color.gray),
}