// Bless be the name of LORD our GOD
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class AdjustmentType(
    val text: String,
    val color: Int,
    val icon: Int,
    val typeDescription: String,
) {
    DEBT_REPAY(
        "Payback",
        icon = R.drawable.outlined_repay, color = R.color.RepayDebt,
        typeDescription = "Payback your debt"
    ),
    LENT_REPAY(
        "Loan Refund",
        icon = R.drawable.outlined_repay, color = R.color.RepayLoan,
        typeDescription = "Refund your loan"
    ),
    GOAL_ATTAIN(
        "Goal Attain",
        icon = R.drawable.oulined_attain, color = R.color.Attain,
        typeDescription = "Attain your goal"
    ),
    INITIAL(
        "Initial",
        icon = R.drawable.outlined_repay, color = R.color.gray,
        typeDescription = "Initial"
    ),
}