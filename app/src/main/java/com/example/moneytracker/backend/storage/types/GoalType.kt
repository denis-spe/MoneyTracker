package com.example.moneytracker.backend.storage.types

import com.example.moneytracker.R

data object GoalType : FinanceCategory {
    override val tagIconRes = R.drawable.tag_goal
    override val text = "Goal"
    override val color = R.color.Goal
    override val outlinedIcon = R.drawable.outlined_goal
    override val filledIcon = R.drawable.filled_goal
    override val typeDescription = "Reach your targets"
}
