package com.example.moneytracker.backend.storage.types

import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.FinanceEntity

data object GoalType : FinanceCategory {
    override val text = FinanceEntity.GOAL_TEXT
    override val color = FinanceEntity.GOAL_COLOR
    override val outlinedIcon = FinanceEntity.GOAL_OUTLINED_ICON
    override val filledIcon = FinanceEntity.GOAL_FILLED_ICON
    override val tagIconRes = R.drawable.tag_goal
    override val typeDescription = FinanceEntity.GOAL_DESCRIPTION
}
