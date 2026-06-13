package com.example.moneytracker.backend.storage.types

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class SettlementType(
    val text: String,
    val color: Int,
    val icon: Int,
    val outlineIcon: Int,
    val typeDescription: String,
) {
    DEBT_REPAY(
        "Payback",
        icon = R.drawable.filled_repay,
        outlineIcon = R.drawable.outlined_repay,
        color = R.color.RepayDebt,
        typeDescription = "Payback your debt"
    ),

    LENT_REPAY(
        "Refund",
        icon = R.drawable.filled_refund,
        outlineIcon = R.drawable.outlined_refund,
        color = R.color.RepayLoan,
        typeDescription = "Refund your loan"
    ),

    GOAL_ATTAIN(
        "Attain",
        icon = R.drawable.filled_goal,
        outlineIcon = R.drawable.filled_attain,
        color = R.color.Attain,
        typeDescription = "Attain your goal"
    ),

    WITHDRAWAL(
        "Withdrawal",
        icon = R.drawable.filled_withdrawal,
        outlineIcon = R.drawable.outline_withdrawal,
        color = R.color.Withdrawal,
        typeDescription = "Transfer between your accounts"
    ),

    INITIAL(
        "Initial",
        icon = R.drawable.filled_repay,
        outlineIcon = R.drawable.outlined_repay,
        color = R.color.gray,
        typeDescription = "Initial"
    ),
}