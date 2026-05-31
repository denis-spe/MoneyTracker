package com.example.moneytracker.backend.storage.types

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class SettlementType(
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
        "Refund",
        icon = R.drawable.outlined_repay, color = R.color.RepayLoan,
        typeDescription = "Refund your loan"
    ),
    GOAL_ATTAIN(
        "Goal Attain",
        icon = R.drawable.oulined_attain, color = R.color.Attain,
        typeDescription = "Attain your goal"
    ),

    WITHDRAWAL(
        "Withdrawal",
        icon = R.drawable.money_transfer,
        color = R.color.Withdrawal,
        typeDescription = "Transfer between your accounts"
    ),

    INITIAL(
        "Initial",
        icon = R.drawable.outlined_repay, color = R.color.gray,
        typeDescription = "Initial"
    ),
}