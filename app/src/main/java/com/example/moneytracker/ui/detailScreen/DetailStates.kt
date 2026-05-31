package com.example.moneytracker.ui.detailScreen

import com.example.moneytracker.backend.storage.CountAchievement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.ui.homeScreen.DataState

data class DetailStates(
    val financeEntity: DataState<FinanceEntity?> = DataState.Loading,
    val countAchievement: DataState<CountAchievement?> = DataState.Loading
)
